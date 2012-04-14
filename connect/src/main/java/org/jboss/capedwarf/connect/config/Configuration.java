package org.jboss.capedwarf.connect.config;

import org.apache.http.HttpVersion;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.jboss.capedwarf.common.Constants;
import org.jboss.capedwarf.connect.server.ServerProxyHandler;
import org.jboss.capedwarf.connect.server.ServerProxyInvocationHandler;

/**
 * The server config.
 *
 * @author Ales Justin
 * @author Marko Strukelj
 */
public abstract class Configuration<T> {
    private static Configuration instance;

    private String hostName;
    private int port;
    private int sslPort;
    private String appContext = "";
    private String clientContext = "client";
    private String secureContext = "secure";
    private boolean isDebugMode;
    private boolean isDebugLogging;
    private boolean isStrictSSL;
    private boolean isStrictPort = true;
    private int connectionTimeout = 30 * (int) Constants.SECOND;
    private HttpVersion httpVersion = HttpVersion.HTTP_1_1;
    private String contentCharset = "UTF-8";
    private SocketFactory plainFactory;
    private SocketFactory sslFactory;
    private Class<T> proxyClass;

    private String httpEndpoint;
    private String sslEndpoint;
    private int soTimeout = 30 * (int) Constants.SECOND;
    private boolean expectContinue = true;
    private boolean staleCheckingEnabled;
    private int socketBufferSize = 8192;

    public synchronized static <T> Configuration<T> getInstance() {
        if (instance == null)
            return new DefaultConfiguration<T>();
        //noinspection unchecked
        return instance;
    }

    public synchronized static <T> void setInstance(Configuration<T> conf) {
        instance = conf;
    }

    public ServerProxyInvocationHandler getServerProxyHandler() {
        return new ServerProxyHandler(this);
    }

    /**
     * Invalidate cached values.
     */
    public void invalidate() {
        httpEndpoint = null;
        sslEndpoint = null;
    }

    protected void validateConfiguration() {
        if (hostName == null)
            throw new IllegalArgumentException("Null host name!");
        if (appContext == null)
            throw new IllegalArgumentException("Null app context!");
    }

    public String getEndpoint(boolean secure) {
        return secure ? getSslEndpoint() : getHttpEndpoint();
    }

    protected String getHttpEndpoint() {
        if (httpEndpoint == null)
            httpEndpoint = createURL(isStrictSSL(), false, port);
        return httpEndpoint;
    }

    protected String getSslEndpoint() {
        if (sslEndpoint == null)
            sslEndpoint = createURL(isDebugMode() == false, true, sslPort);
        return sslEndpoint;
    }

    protected String createURL(boolean ssl, boolean secure, int xport) {
        validateConfiguration();

        StringBuilder endpointUrl = new StringBuilder("http");
        if (ssl)
            endpointUrl.append('s');

        int pos = hostName.indexOf("://");
        if (pos != -1) {
            // cut off after protocol spec
            if (pos > 0)
                hostName = hostName.substring(pos);
        } else {
            // prepend ://
            endpointUrl.append("://");
        }
        endpointUrl.append(hostName);
        if (hostName.endsWith("/"))
            endpointUrl.deleteCharAt(endpointUrl.length() - 1);

        if (isStrictPort())
            endpointUrl.append(':').append(xport);

        endpointUrl.append("/").append(getAppContext());
        if (getAppContext().length() > 0)
            endpointUrl.append("/");
        endpointUrl.append(getClientContext()).append("/");
        if (secure)
            endpointUrl.append(getSecureContext()).append("/");

        return endpointUrl.toString();
    }

    public Class<T> getProxyClass() {
        if (proxyClass == null)
            throw new IllegalArgumentException("Null proxy class");
        return proxyClass;
    }

    public void setProxyClass(Class<T> proxyClass) {
        this.proxyClass = proxyClass;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSslPort() {
        return sslPort;
    }

    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }

    public String getAppContext() {
        return appContext;
    }

    public void setAppContext(String appContext) {
        this.appContext = appContext;
    }

    public String getClientContext() {
        return clientContext;
    }

    public void setClientContext(String clientContext) {
        this.clientContext = clientContext;
    }

    public String getSecureContext() {
        return secureContext;
    }

    public void setSecureContext(String secureContext) {
        this.secureContext = secureContext;
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void setDebugMode(boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
    }

    public boolean isDebugLogging() {
        return isDebugLogging;
    }

    public void setDebugLogging(boolean isDebugLogging) {
        this.isDebugLogging = isDebugLogging;
    }

    public boolean isStrictSSL() {
        return isStrictSSL;
    }

    public void setStrictSSL(boolean strictSSL) {
        isStrictSSL = strictSSL;
    }

    public boolean isStrictPort() {
        return isStrictPort;
    }

    public void setStrictPort(boolean strictPort) {
        isStrictPort = strictPort;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getContentCharset() {
        return contentCharset;
    }

    public void setContentCharset(String contentCharset) {
        this.contentCharset = contentCharset;
    }

    public SocketFactory getPlainFactory() {
        if (plainFactory == null)
            return PlainSocketFactory.getSocketFactory();

        return plainFactory;
    }

    public void setPlainFactory(SocketFactory plainFactory) {
        this.plainFactory = plainFactory;
    }

    public SocketFactory getSslFactory() {
        if (sslFactory == null)
            return SSLSocketFactory.getSocketFactory();

        return sslFactory;
    }

    public void setSslFactory(SocketFactory sslFactory) {
        this.sslFactory = sslFactory;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public boolean isExpectContinue() {
        return expectContinue;
    }

    public void setExpectContinue(boolean expectContinue) {
        this.expectContinue = expectContinue;
    }

    public boolean isStaleCheckingEnabled() {
        return staleCheckingEnabled;
    }

    public void setStaleCheckingEnabled(boolean staleCheckingEnabled) {
        this.staleCheckingEnabled = staleCheckingEnabled;
    }

    public int getSocketBufferSize() {
        return socketBufferSize;
    }

    public void setSocketBufferSize(int socketBufferSize) {
        this.socketBufferSize = socketBufferSize;
    }
}
