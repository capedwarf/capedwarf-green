package org.jboss.capedwarf.connect.server;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.jboss.capedwarf.common.Constants;
import org.jboss.capedwarf.common.env.Environment;
import org.jboss.capedwarf.common.env.EnvironmentFactory;
import org.jboss.capedwarf.common.env.Secure;
import org.jboss.capedwarf.common.io.ClosedInputStream;
import org.jboss.capedwarf.common.io.FixedLengthInputStream;
import org.jboss.capedwarf.common.serialization.*;
import org.jboss.capedwarf.connect.config.Configuration;
import org.jboss.capedwarf.validation.ValidationHelper;

import javax.validation.constraints.Size;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

/**
 * ServerProxy handler.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ServerProxyHandler implements ServerProxyInvocationHandler {
    /**
     * The client
     */
    private HttpClient client;

    /**
     * The query cache
     */
    private Map<String, QueryInfo> queryCache = new HashMap<String, QueryInfo>();

    /**
     * The environment
     */
    private volatile Environment env;

    private Configuration config;

    private boolean allowsStreaming;

    public ServerProxyHandler(Configuration config) {
        if (config == null)
            throw new IllegalArgumentException("Null configuration");
        this.config = config;
    }

    /**
     * Is streaming allowed.
     *
     * @param allowsStreaming the streaming flag
     */
    public void setAllowsStreaming(boolean allowsStreaming) {
        this.allowsStreaming = allowsStreaming;
    }

    /**
     * Get client.
     *
     * @return the client
     */
    private synchronized HttpClient getClient() {
        if (client == null) {
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, config.getConnectionTimeout());
            HttpProtocolParams.setVersion(params, config.getHttpVersion());
            HttpProtocolParams.setContentCharset(params, config.getContentCharset());

            // Create and initialize scheme registry
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", config.getPlainFactory(), config.getPort()));
            schemeRegistry.register(new Scheme("https", config.getSslFactory(), config.getSslPort()));

            ClientConnectionManager ccm = createClientConnectionManager(params, schemeRegistry);

            client = createClient(ccm, params);
        }

        return client;
    }

    /**
     * Create client connection manager.
     * <p/>
     * Create an HttpClient with the ThreadSafeClientConnManager.
     * This connection manager must be used if more than one thread will
     * be using the HttpClient.
     *
     * @param params         the http params
     * @param schemeRegistry the scheme registry
     * @return new client connection manager
     */
    protected ClientConnectionManager createClientConnectionManager(HttpParams params, SchemeRegistry schemeRegistry) {
        return new ThreadSafeClientConnManager(params, schemeRegistry);
    }

    /**
     * Create new http client.
     *
     * @param ccm    the client connection manager
     * @param params the http params
     * @return new http client
     */
    protected HttpClient createClient(ClientConnectionManager ccm, HttpParams params) {
        return new DefaultHttpClient(ccm, params);
    }

    public synchronized void shutdown() {
        if (client != null) {
            HttpClient tmp = client;
            client = null; // nullify

            ClientConnectionManager manager = tmp.getConnectionManager();
            if (manager != null)
                manager.shutdown();
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return doInvoke(proxy, method, args);
        } finally {
            HttpHeaders.clear();
        }
    }

    @SuppressWarnings("UnusedParameters")
    protected Object doInvoke(Object proxy, Method method, final Object[] args) throws Throwable {
        Class<?> declaringClass = method.getDeclaringClass();
        if (declaringClass == Object.class) {
            return null; // only handle ServerProxy methods
        }
        if (ServerProxyHandle.class.equals(declaringClass)) {
            if ("setAllowsStreaming".equals(method.getName()))
                setAllowsStreaming((Boolean) args[0]);
            else if ("shutdown".equals(method.getName()))
                shutdown();

            return null;
        }

        final QueryInfo query = createQuery(method, args);

        final boolean isGzipDisabled = GzipOptionalSerializator.isGzipEnabled() == false;
        if (query.gzip && isGzipDisabled)
            GzipOptionalSerializator.enableGzip();

        try {
            final ResultProducer rp;
            if (query.jsonAware) {
                final List<JSONAware> toJSON = new ArrayList<JSONAware>();
                for (Object arg : args) {
                    if (JSONAware.class.isInstance(arg)) {
                        ValidationHelper.validate(arg);
                        toJSON.add(JSONAware.class.cast(arg));
                    }
                }
                rp = new ResultProducer() {
                    public Result run() throws Throwable {
                        return getContent(query, toJSON);
                    }
                };
            } else if (query.directContent >= 0) {
                final int index = query.directContent;
                if (args[index] instanceof ContentProducer) {
                    rp = new ResultProducer() {
                        public Result run() throws Throwable {
                            return getResultWithContentProducer(query, (ContentProducer) args[index]);
                        }
                    };
                } else if (args[index] instanceof HttpEntity) {
                    rp = new ResultProducer() {
                        public Result run() throws Throwable {
                            return getResultWithHttpEntity(query, (HttpEntity) args[index]);
                        }
                    };
                } else {
                    throw new IllegalArgumentException("Cannot create ResultProducer, illegal argument: " + Arrays.toString(args));
                }
            } else {
                rp = new ResultProducer() {
                    public Result run() throws Throwable {
                        return getResultWithHttpEntity(query, null);
                    }
                };
            }

            Result result = wrapResult(rp.run());
            InputStream content = result.stream;
            try {
                if (result.status != 200) {
                    packResponseError(method, content, result.status);
                }
                FixedLengthInputStream fis = new FixedLengthInputStream(content, result.contentLength);
                Object retVal;
                retVal = toValue(method, fis);
                if (retVal instanceof InputStream) {
                    return new ProgressInputStream((InputStream) retVal, fis);
                }
                return retVal;
            } catch (Throwable t) {
                // Lets retry if we're over GAE limit
                if (result.executionTime > 29 * 1000) {
                    getEnv().log(Constants.TAG_CONNECTION, Level.CONFIG, "Retrying, hit GAE limit: " + (result.executionTime / 1000), null);
                    result = wrapResult(rp.run());
                    if (result.status != 200) {
                        packResponseError(method, result.stream, result.status);
                    }
                    FixedLengthInputStream fis = new FixedLengthInputStream(result.stream, result.contentLength);
                    Object retVal;
                    retVal = toValue(method, fis);
                    if (retVal instanceof InputStream) {
                        return new ProgressInputStream((InputStream) retVal, fis);
                    }
                    return retVal;

                } else {
                    throw t;
                }
            }
        } finally {
            if (query.gzip && isGzipDisabled)
                GzipOptionalSerializator.disableGzip();
        }
    }

    /**
     * Wrap result.
     * e.g. possible test env
     *
     * @param result the result
     * @return modified result
     */
    protected Result wrapResult(Result result) {
        return result;
    }

    /**
     * Get environment.
     *
     * @return the environment
     */
    protected Environment getEnv() {
        if (env == null)
            env = EnvironmentFactory.getEnvironment();

        return env;
    }

    /**
     * Get content as a input stream.
     *
     * @param qi   the additional client query info
     * @param args the args to serialize
     * @return the response input stream
     * @throws Throwable for any error
     */
    @SuppressWarnings({"unchecked"})
    protected Result getContent(QueryInfo qi, final List<JSONAware> args) throws Throwable {
        ContentProducer cp = null;
        if (qi.jsonAware && args.isEmpty() == false) {
            HttpHeaders.setContentType("application/json");
            cp = new ContentProducer() {
                public void writeTo(OutputStream outputStream) throws IOException {
                    Serializator serializator;
                    if (args.size() == 1) {
                        if (config.isDebugLogging()) {
                            serializator = JSONSerializator.OPTIONAL_GZIP_BUFFERED;
                        } else {
                            serializator = JSONSerializator.OPTIONAL_GZIP;
                        }
                        serializator.serialize(args.get(0), outputStream);
                    } else {
                        serializator = new GzipOptionalSerializator(
                                new MultiJSONCollectionSerializator(
                                        new ReflectionJSONAwareInstanceProvider(new ElementTypeProvider() {
                                            public Class getType(int index) {
                                                return args.get(index).getClass();
                                            }
                                        })
                                )
                        );
                        serializator.serialize(args, outputStream);
                    }
                    outputStream.flush();
                }
            };
        }
        return getResultWithContentProducer(qi, cp);
    }

    /**
     * Get content as a input stream.
     *
     * @param qi the additional client query info
     * @param cp the content producer
     * @return the response input stream
     * @throws Throwable for any error
     */
    protected Result getResultWithContentProducer(QueryInfo qi, ContentProducer cp) throws Throwable {
        HttpEntity entity = null;
        if (cp != null) {
            if (allowsStreaming) {
                entity = new EntityTemplate(cp);
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                cp.writeTo(baos);
                entity = new ByteArrayEntity(baos.toByteArray());
            }
        }
        return getResultWithHttpEntity(qi, entity);
    }

    /**
     * Get content as a input stream.
     *
     * @param qi     the additional client query info
     * @param entity the http entity
     * @return the response input stream
     * @throws Throwable for any error
     */
    protected Result getResultWithHttpEntity(QueryInfo qi, HttpEntity entity) throws Throwable {
        String link = config.getEndpoint(qi.secure) + qi.query;
        if (config.isDebugLogging())
            getEnv().log(Constants.TAG_CONNECTION, Level.INFO, "URL: " + link, null);

        HttpPost httppost = new HttpPost(link);

        if (entity != null)
            httppost.setEntity(entity);

        for (Header header : HttpHeaders.getHeaders()) {
            httppost.addHeader(header);
        }

        if (qi.secure) {
            Environment env = EnvironmentFactory.getEnvironment();
            long id = env.getUserId();
            String token = env.getUserToken();
            httppost.addHeader(Constants.CLIENT_ID, String.valueOf(id));
            httppost.addHeader(Constants.CLIENT_TOKEN, token);
        }

        // disable gzip
        if (GzipOptionalSerializator.isGzipEnabled()) {
            // TODO -- httppost.addHeader(Constants.IGNORE_GZIP, Boolean.TRUE.toString());
        }

        Result result = new Result();
        try {
            // invoke the post / request
            HttpResponse response = getClient().execute(httppost);
            result.status = response.getStatusLine().getStatusCode();
            Header h = response.getFirstHeader("Content-Length");
            if (h != null)
                result.contentLength = Long.parseLong(h.getValue());
            result.stream = response.getEntity().getContent();
        } finally {
            result.end();
        }
        return result;
    }

    /**
     * Get ssl.
     *
     * @param qi the query info
     * @return true if ssl, false otherwise
     */
    protected boolean getSSL(QueryInfo qi) {
        return (config.isDebugMode() == false && qi.secure) || config.isStrictSSL();
    }

    /**
     * Create query.
     *
     * @param method the method
     * @param args   the args
     * @return new query
     */
    protected QueryInfo createQuery(Method method, Object[] args) {
        QueryInfo value;
        Query query = method.getAnnotation(Query.class);
        if (query != null) {
            value = new QueryInfo();
            value.query = query.value();
            value.jsonAware = query.jsonAware();
            value.secure = method.isAnnotationPresent(Secure.class);
            value.gzip = isGzip(method);
        } else {
            String methodName = method.getName();
            Class<?>[] pt = method.getParameterTypes();

            boolean[] notNullChecks = new boolean[pt.length];
            // validate the arguments
            for (int i = 0; i < pt.length; i++) {
                notNullChecks[i] = (args[i] != null);

                // send over just the ordinal
                if (notNullChecks[i] && (args[i] instanceof Enum<?>)) {
                    Enum<?> e = (Enum<?>) args[i];
                    args[i] = e.ordinal();
                }
            }

            String key = methodName + Arrays.toString(pt);
            value = queryCache.get(key);
            if (value == null) {
                boolean jsonAware = false;
                int directContent = -1;
                StringBuilder builder = buildPath(method);
                Annotation[][] pa = method.getParameterAnnotations();
                for (int i = 0; i < pt.length; i++) {
                    Annotation[] ppa = pa[i];
                    if (ppa == null || ppa.length == 0) {
                        if (notNullChecks[i] == false)
                            throw new IllegalArgumentException("Null non-query (JSON, ...) aware parameter: " + i);

                        if (JSONAware.class.isAssignableFrom(pt[i]) == false) {
                            if (ContentProducer.class.isAssignableFrom(pt[i]) || HttpEntity.class.isAssignableFrom(pt[i])) {
                                if (directContent >= 0)
                                    throw new IllegalArgumentException("Only 1 non-JSONAware argument allowed: " + Arrays.toString(pt));

                                directContent = i;
                            } else {
                                throw new IllegalArgumentException("Illegal method parameter, missing QueryParameter? - " + method);
                            }
                        }

                        jsonAware = jsonAware || (JSONAware.class.isAssignableFrom(pt[i]));

                        if (jsonAware && directContent >= 0)
                            throw new IllegalArgumentException("Cannot have both - JSON and streaming: " + Arrays.toString(pt));

                    } else {
                        for (Annotation a : ppa) {
                            if (a instanceof Size) {
                                Size size = (Size) a;
                                ValidationHelper.validate(size, args[i]);
                            } else if (isQueryParameter(a)) {
                                if ((notNullChecks[i] == false) && isQueryParameterRequired(a))
                                    throw new IllegalArgumentException("Argument is required, but it's null: " + getQueryParameterValue(a));

                                if (notNullChecks[i]) {
                                    String m = (builder.indexOf("?") >= 0) ? "&" : "?";
                                    builder.append(m).append(getQueryParameterValue(a)).append("=").append("%").append(i + 1).append("$1s");
                                }
                            }
                        }

                        // re-format iterables
                        if (notNullChecks[i] && (args[i] instanceof Iterable))
                            args[i] = new CommaListedFormattable((Iterable) args[i]);
                    }
                }
                value = new QueryInfo();
                value.query = builder.toString();
                value.jsonAware = jsonAware;
                value.directContent = directContent;
                value.secure = method.isAnnotationPresent(Secure.class);
                value.gzip = isGzip(method);
                queryCache.put(key, value);
            }
        }
        // create result
        QueryInfo result = new QueryInfo();
        result.query = new Formatter().format(value.query, args).toString();
        result.jsonAware = value.jsonAware;
        result.directContent = value.directContent;
        result.secure = value.secure;
        result.gzip = value.gzip;
        return result;
    }

    protected boolean isQueryParameter(Annotation a) {
        return (a instanceof QueryParameter);
    }

    protected boolean isQueryParameterRequired(Annotation a) {
        return (a instanceof QueryParameter) && (QueryParameter.class.cast(a).required());
    }

    protected String getQueryParameterValue(Annotation a) {
        if (a instanceof QueryParameter) {
            return QueryParameter.class.cast(a).value();
        } else {
            throw new IllegalArgumentException("Forgot to override this method?!");
        }
    }

    protected StringBuilder buildPath(Method method) {
        boolean type = false;
        StringBuilder builder = new StringBuilder();
        char[] chars = method.getName().toCharArray();
        for (char ch : chars) {
            if (Character.isUpperCase(ch)) {
                if (type)
                    builder.append('-');
                else {
                    type = true;
                    builder.append("?action=");
                }
            }
            builder.append(Character.toLowerCase(ch));
        }
        return builder;
    }

    /**
     * Do we use gzip.
     *
     * @param method the method
     * @return true if gzip, false otherwise
     */
    protected boolean isGzip(Method method) {
        Gzip gzip = method.getAnnotation(Gzip.class);
        return (gzip != null) && (gzip.value() == GzipType.ENABLE);
    }

    /**
     * Get collection element type.
     *
     * @param method the method
     * @return the element's type
     */
    protected Class<?> elementType(Method method) {
        Type rt = method.getGenericReturnType();
        if (rt instanceof ParameterizedType == false)
            throw new IllegalArgumentException("Cannot get exact type: " + rt);

        ParameterizedType pt = (ParameterizedType) rt;
        Type[] ats = pt.getActualTypeArguments();
        if (ats == null || ats.length != 1 || (ats[0] instanceof Class == false))
            throw new IllegalArgumentException("Illegal actual type: " + Arrays.toString(ats));

        return (Class) ats[0];
    }

    /**
     * Handle non-200 response code.
     *
     * @param method the method
     * @param stream the stream
     * @param status the status
     * @throws Throwable for any error
     */
    protected void packResponseError(Method method, InputStream stream, int status) throws Throwable {
        Object value;
        try {
            value = toValue(method, stream);
        } catch (Exception ex) {
            throw new RuntimeException("Server error [status: " + status + "] :: ", ex);
        }
        throw new RuntimeException("Server error [status: " + status + "] :: " + value);
    }

    /**
     * Get value from response.
     *
     * @param method  the method
     * @param content the content
     * @return response's value
     * @throws Throwable for any error
     */
    protected Object toValue(Method method, InputStream content) throws Throwable {
        boolean closeOnReturn = true;
        content = new ClosedInputStream(content);
        try {
            Class<?> rt = method.getReturnType(); // return type
            if (Collection.class.isAssignableFrom(rt)) {
                Class<?> elementClass = elementType(method);
                if (JSONAware.class.isAssignableFrom(elementClass)) {
                    @SuppressWarnings({"unchecked"})
                    Class<? extends JSONAware> jsonClass = (Class<? extends JSONAware>) elementClass;
                    Serializator serializator = new GzipOptionalSerializator(new BufferedSerializator(new JSONCollectionSerializator(jsonClass)));
                    if (Set.class.isAssignableFrom(rt))
                        return serializator.deserialize(content, HashSet.class);
                    else
                        return serializator.deserialize(content, ArrayList.class);
                } else {
                    String value = convertStreamToString(content);
                    String[] split = value.split(",");
                    Collection<Object> result;
                    if (Set.class.isAssignableFrom(rt))
                        result = new HashSet<Object>();
                    else
                        result = new ArrayList<Object>();
                    for (String s : split) {
                        result.add(ConverterUtils.toValue(elementClass, s));
                    }
                    return result;
                }
            } else if (JSONAware.class.isAssignableFrom(rt)) {
                return JSONSerializator.OPTIONAL_GZIP_BUFFERED.deserialize(content, rt);
            } else if (InputStream.class.isAssignableFrom(rt)) {
                closeOnReturn = false;
                return GzipOptionalSerializator.wrap(content);
            } else {
                String value = convertStreamToString(content);
                return ConverterUtils.toValue(rt, value);
            }
        } finally {
            try {
                if (closeOnReturn)
                    content.close();
            } catch (IOException ignored) {
            }
        }
    }

    static String convertStreamToString(InputStream is) throws IOException {
        /*
        * To convert the InputStream to String we use the BufferedReader.readLine()
        * method. We iterate until the BufferedReader return null which means
        * there's no more data to read. Each line will appended to a StringBuilder
        * and returned as String.
        */
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    protected static class QueryInfo {
        private String query;
        private boolean jsonAware;
        private int directContent = -1;
        private boolean secure;
        private boolean gzip;
    }

    protected static class Result {
        private int status;
        private InputStream stream;
        private long executionTime;
        private long contentLength = -1;

        private Result() {
            executionTime = System.currentTimeMillis();
        }

        public void end() {
            executionTime = System.currentTimeMillis() - executionTime;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    protected interface ResultProducer {
        Result run() throws Throwable;
    }
}
