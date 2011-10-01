package org.jboss.capedwarf.connect.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.validation.constraints.Size;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
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
import org.jboss.capedwarf.common.serialization.BufferedSerializator;
import org.jboss.capedwarf.common.serialization.ConverterUtils;
import org.jboss.capedwarf.common.serialization.ElementTypeProvider;
import org.jboss.capedwarf.common.serialization.GzipOptionalSerializator;
import org.jboss.capedwarf.common.serialization.JSONAware;
import org.jboss.capedwarf.common.serialization.JSONCollectionSerializator;
import org.jboss.capedwarf.common.serialization.JSONSerializator;
import org.jboss.capedwarf.common.serialization.MultiJSONCollectionSerializator;
import org.jboss.capedwarf.common.serialization.ReflectionJSONAwareInstanceProvider;
import org.jboss.capedwarf.common.serialization.Serializator;
import org.jboss.capedwarf.connect.config.Configuration;
import org.jboss.capedwarf.validation.ValidationHelper;

/**
 * ServerProxy handler.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ServerProxyHandler implements ServerProxyInvocationHandler
{
   /** The client */
   private HttpClient client;

   /** The query cache */
   private Map<String, QueryInfo> queryCache = new HashMap<String, QueryInfo>();

   /** The environment */
   private volatile Environment env;   

   private String endpointUrl;
   
   private Configuration config;

   private boolean allowsStreaming;

   public ServerProxyHandler(Configuration config)
   {
      if (config == null)
         throw new IllegalArgumentException("Null configuration");

      endpointUrl = config.getHostName();
      
      int pos = endpointUrl.indexOf("://");
      if (pos != -1)
      {
         // cut off after protocol spec
         if (pos > 0)
            endpointUrl = endpointUrl.substring(pos);
      }
      else if (pos == -1)
      {
         // prepend ://
         endpointUrl = "://" + endpointUrl;
      }
      
      // it must end with /client/
      if (endpointUrl.endsWith("/") == false)
         endpointUrl += "/";

      endpointUrl += "client/";
      
      this.config = config;
   }

   /**
    * Is streaming allowed.
    *
    * @param allowsStreaming the streaming flag
    */
   public void setAllowsStreaming(boolean allowsStreaming)
   {
      this.allowsStreaming = allowsStreaming;
   }

   /**
    * Get client.
    *
    * @return the client
    */
   private synchronized HttpClient getClient()
   {
      if (client == null)
      {
         HttpParams params = new BasicHttpParams();
         HttpConnectionParams.setConnectionTimeout(params, 30 * (int)Constants.SECOND);
         HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
         HttpProtocolParams.setContentCharset(params, "UTF-8");

         // Create and initialize scheme registry
         SchemeRegistry schemeRegistry = new SchemeRegistry();
         schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), config.getPort()));
         schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), config.getSslPort()));

         // Create an HttpClient with the ThreadSafeClientConnManager.
         // This connection manager must be used if more than one thread will
         // be using the HttpClient.
         ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, schemeRegistry);

         client = new DefaultHttpClient(ccm, params);
      }

      return client;
   }

   public synchronized void shutdown()
   {
      if (client != null)
      {
         HttpClient tmp = client;
         client = null; // nullify

         ClientConnectionManager manager = tmp.getConnectionManager();
         if (manager != null)
            manager.shutdown();
      }
   }

   public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable
   {
      Class<?> declaringClass = method.getDeclaringClass();
      if (declaringClass == Object.class)
      {
         return null; // only handle ServerProxy methods
      }
      if (ServerProxyHandle.class.equals(declaringClass))
      {
         if ("setAllowsStreaming".equals(method.getName()))
            setAllowsStreaming((Boolean)args[0]);
         else if ("shutdown".equals(method.getName()))
            shutdown();

         return null;
      }

      final QueryInfo query = createQuery(method, args);

      ResultProducer rp;
      if (query.jsonAware)
      {
         final List<JSONAware> toJSON = new ArrayList<JSONAware>();
         for (Object arg : args)
         {
            if (JSONAware.class.isInstance(arg))
            {
               ValidationHelper.validate(arg);
               toJSON.add(JSONAware.class.cast(arg));
            }
         }
         rp = new ResultProducer()
         {
            public Result run() throws Throwable
            {
               return getContent(query, toJSON);
            }
         };
      }
      else if (query.directContent)
      {
         if (args[0] instanceof ContentProducer)
         {
            rp = new ResultProducer()
            {
               public Result run() throws Throwable
               {
                  return getResultWithContentProducer(query, (ContentProducer) args[0]);
               }
            };
         }
         else if (args[0] instanceof HttpEntity)
         {
            rp = new ResultProducer()
            {
               public Result run() throws Throwable
               {
                  return getResultWithHttpEntity(query, (HttpEntity) args[0]);
               }
            };
         }
         else
         {
            throw new IllegalArgumentException("Cannot create ResultProducer, illegal argument: " + Arrays.toString(args));
         }
      }
      else
      {
         rp = new ResultProducer()
         {
            public Result run() throws Throwable
            {
               return getResultWithHttpEntity(query, null);
            }
         };
      }

      Result result = wrapResult(rp.run());
      InputStream content = result.stream;
      try
      {
         if (result.status != 200)
         {
            packResponseError(method, content, result.status);
         }
         return toValue(method, content);
      }
      catch (Throwable t)
      {
         // Lets retry if we're over GAE limit
         if (result.executionTime > 29 * 1000)
         {
            getEnv().log(Constants.TAG_CONNECTION, Level.CONFIG, "Retrying, hit GAE limit: " + (result.executionTime / 1000), null);
            result = wrapResult(rp.run());
            if (result.status != 200)
            {
               packResponseError(method, result.stream, result.status);
            }
            return toValue(method, result.stream);
         }
         else
         {
            throw t;
         }
      }
   }

   /**
    * Wrap result.
    * e.g. possible test env
    *
    * @param result the result
    * @return modified result
    */
   protected Result wrapResult(Result result)
   {
      return result;
   }

   /**
    * Get environment.
    *
    * @return the environment
    */
   protected Environment getEnv()
   {
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
   protected Result getContent(QueryInfo qi, final List<JSONAware> args) throws Throwable
   {
      ContentProducer cp = null;
      if (qi.jsonAware && args.isEmpty() == false)
      {
         cp = new ContentProducer()
         {
            public void writeTo(OutputStream outputStream) throws IOException
            {
               Serializator serializator;
               if (args.size() == 1)
               {
                  if (config.isDebugLogging())
                  {
                     serializator = JSONSerializator.OPTIONAL_GZIP_BUFFERED;
                  }
                  else
                  {
                     serializator = JSONSerializator.OPTIONAL_GZIP;
                  }
                  serializator.serialize(args.get(0), outputStream);
               }
               else
               {
                  serializator = new GzipOptionalSerializator(
                        new MultiJSONCollectionSerializator(
                              new ReflectionJSONAwareInstanceProvider(new ElementTypeProvider()
                              {
                                 public Class getType(int index)
                                 {
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
    * @param qi   the additional client query info
    * @param cp   the content producer
    * @return the response input stream
    * @throws Throwable for any error
    */
   protected Result getResultWithContentProducer(QueryInfo qi, ContentProducer cp) throws Throwable
   {
      HttpEntity entity = null;
      if (cp != null)
      {
         if (allowsStreaming)
         {
            entity = new EntityTemplate(cp);
         }
         else
         {
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
    * @param qi   the additional client query info
    * @param entity the http entity
    * @return the response input stream
    * @throws Throwable for any error
    */
   protected Result getResultWithHttpEntity(QueryInfo qi, HttpEntity entity) throws Throwable
   {
      String link = "http" + (getSecure(qi) ? "s" : "") + endpointUrl + (qi.secure ? "secure/" : "") + qi.query;
      if (config.isDebugLogging())
         getEnv().log(Constants.TAG_CONNECTION, Level.INFO, "URL: " + link, null);

      HttpPost httppost = new HttpPost(link);

      if (entity != null)
         httppost.setEntity(entity);

      if (qi.secure)
      {
         Environment env = EnvironmentFactory.getEnvironment();
         long id = env.getUserId();
         String token = env.getUserToken();
         httppost.addHeader(Constants.CLIENT_ID, String.valueOf(id));
         httppost.addHeader(Constants.CLIENT_TOKEN, token);
      }

      // disable gzip
      if (GzipOptionalSerializator.isGzipDisabled())
      {
         httppost.addHeader(Constants.IGNORE_GZIP, Boolean.TRUE.toString());
      }

      Result result = new Result();
      try
      {
         // invoke the post / request
         HttpResponse response = getClient().execute(httppost);
         result.status = response.getStatusLine().getStatusCode();
         result.stream = response.getEntity().getContent();
      }
      finally
      {
         result.end();
      }
      return result;
   }

   /**
    * Get secure.
    *
    * @param qi the query info
    * @return true if secure, false otherwise
    */
   private boolean getSecure(QueryInfo qi)
   {
      return config.isDebugMode() == false && qi.secure;
   }

   /**
    * Create query.
    *
    * @param method the method
    * @param args   the args
    * @return new query
    */
   protected QueryInfo createQuery(Method method, Object[] args)
   {
      QueryInfo value;
      Query query = method.getAnnotation(Query.class);
      if (query != null)
      {
         value = new QueryInfo();
         value.query = query.value();
         value.jsonAware = query.jsonAware();
         value.secure = method.isAnnotationPresent(Secure.class);
      }
      else
      {
         String methodName = method.getName();
         Class<?>[] pt = method.getParameterTypes();

         boolean[] notNullChecks = new boolean[pt.length];
         // validate the arguments
         for (int i = 0; i < pt.length; i++)
         {
            notNullChecks[i] = (args[i] != null);

            // send over just the ordinal
            if (notNullChecks[i] && (args[i] instanceof Enum<?>))
            {
               Enum<?> e = (Enum<?>) args[i];
               args[i] = e.ordinal();
            }
         }

         String key = methodName + Arrays.toString(pt);
         value = queryCache.get(key);
         if (value == null)
         {
            boolean type = false;
            boolean jsonAware = false;
            boolean directContent = false;
            StringBuilder builder = new StringBuilder();
            char[] chars = methodName.toCharArray();
            for (char ch : chars)
            {
               if (Character.isUpperCase(ch))
               {
                  if (type)
                     builder.append('-');
                  else
                  {
                     type = true;
                     builder.append("?action=");
                  }
               }
               builder.append(Character.toLowerCase(ch));
            }
            Annotation[][] pa = method.getParameterAnnotations();
            for (int i = 0; i < pt.length; i++)
            {
               Annotation[] ppa = pa[i];
               if (ppa == null || ppa.length == 0)
               {
                  if (notNullChecks[i] == false)
                     throw new IllegalArgumentException("Null non-query (JSON, ...) aware parameter: " + i);

                  if (JSONAware.class.isAssignableFrom(pt[i]) == false)
                  {
                     if (ContentProducer.class.isAssignableFrom(pt[i]) || HttpEntity.class.isAssignableFrom(pt[i]))
                     {
                        if (pt.length > 1)
                           throw new IllegalArgumentException("Only 1 non-JSONAware argument allowed: " + Arrays.toString(pt));

                        directContent = true;
                     }
                     else
                     {
                        throw new IllegalArgumentException("Illegal method parameter, missing QueryParameter? - " + method);
                     }
                  }

                  jsonAware = jsonAware || (JSONAware.class.isAssignableFrom(pt[i]));
               }
               else
               {
                  for (Annotation a : ppa)
                  {
                     if (a instanceof QueryParameter)
                     {
                        QueryParameter qp = (QueryParameter) a;

                        if ((notNullChecks[i] == false) && qp.required())
                           throw new IllegalArgumentException("Argument is required, but it's null: " + qp.value());

                        if (notNullChecks[i])
                           builder.append("&").append(qp.value()).append("=").append("%").append(i + 1).append("$1s");
                     }
                     else if (a instanceof Size)
                     {
                        Size size = (Size) a;
                        ValidationHelper.validate(size, args[i]);
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
            queryCache.put(key, value);
         }
      }
      // create result
      QueryInfo result = new QueryInfo();
      result.query = new Formatter().format(value.query, args).toString();
      result.jsonAware = value.jsonAware;
      result.directContent = value.directContent;
      result.secure = value.secure;
      return result;
   }

   /**
    * Get collection element type.
    *
    * @param method the method
    * @return the element's type
    */
   protected Class<?> elementType(Method method)
   {
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
   protected void packResponseError(Method method, InputStream stream, int status) throws Throwable
   {
      Object value;
      try
      {
         value = toValue(method, stream);
      }
      catch (Exception ex)
      {
         throw new RuntimeException("Server error [status: " + status + "] :: ", ex);
      }
      throw new RuntimeException("Server error [status: " + status + "] :: " + value);
   }

   /**
    * Get value from response.
    *
    * @param method the method
    * @param content the content
    * @return response's value
    * @throws Throwable for any error
    */
   protected Object toValue(Method method, InputStream content) throws Throwable
   {
      boolean closeOnReturn = true;
      content = new ClosedInputStream(content);
      try
      {
         Class<?> rt = method.getReturnType(); // return type
         if (Collection.class.isAssignableFrom(rt))
         {
            Class<?> elementClass = elementType(method);
            if (JSONAware.class.isAssignableFrom(elementClass))
            {
               @SuppressWarnings({"unchecked"})
               Class<? extends JSONAware> jsonClass = (Class<? extends JSONAware>) elementClass;
               Serializator serializator = new GzipOptionalSerializator(new BufferedSerializator(new JSONCollectionSerializator(jsonClass)));
               if (Set.class.isAssignableFrom(rt))
                  return serializator.deserialize(content, HashSet.class);
               else
                  return serializator.deserialize(content, ArrayList.class);
            }
            else
            {
               String value = convertStreamToString(content);
               String[] split = value.split(",");
               Collection<Object> result;
               if (Set.class.isAssignableFrom(rt))
                  result = new HashSet<Object>();
               else
                  result = new ArrayList<Object>();
               for (String s : split)
               {
                  result.add(ConverterUtils.toValue(elementClass, s));
               }
               return result;
            }
         }
         else if (JSONAware.class.isAssignableFrom(rt))
         {
            return JSONSerializator.OPTIONAL_GZIP_BUFFERED.deserialize(content, rt);
         }
         else if (InputStream.class.isAssignableFrom(rt))
         {
            closeOnReturn = false;
            return GzipOptionalSerializator.wrap(content);
         }
         else
         {
            String value = convertStreamToString(content);
            return ConverterUtils.toValue(rt, value);
         }
      }
      finally
      {
         try
         {
            if (closeOnReturn)
               content.close();
         }
         catch (IOException ignored)
         {
         }
      }
   }

   static String convertStreamToString(InputStream is) throws IOException
   {
      /*
      * To convert the InputStream to String we use the BufferedReader.readLine()
      * method. We iterate until the BufferedReader return null which means
      * there's no more data to read. Each line will appended to a StringBuilder
      * and returned as String.
      */
      if (is != null)
      {
         StringBuilder sb = new StringBuilder();
         String line;

         try
         {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null)
            {
               sb.append(line);
            }
         }
         finally
         {
            is.close();
         }
         return sb.toString();
      }
      else
      {
         return "";
      }
   }

   protected static class QueryInfo
   {
      private String query;
      private boolean jsonAware;
      private boolean directContent;
      private boolean secure;
   }

   protected static class Result
   {
      private int status;
      private InputStream stream;
      private long executionTime;

      private Result()
      {
         executionTime = System.currentTimeMillis();
      }

      public void end()
      {
         executionTime = System.currentTimeMillis() - executionTime;
      }

      public void setStatus(int status)
      {
         this.status = status;
      }
   }

   protected interface ResultProducer
   {
      Result run() throws Throwable;
   }
}
