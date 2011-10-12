package org.jboss.capedwarf.connect.config;

/**
 * The server config.
 *
 * @author Ales Justin
 * @author Marko Strukelj
 */
public abstract class Configuration<T>
{
   private static Configuration instance;
   
   private String hostName;
   private int port;
   private int sslPort;   
   private boolean isDebugMode;
   private boolean isDebugLogging;
   private boolean isStrictSSL;
   private Class<T> proxyClass;

   public synchronized static <T> Configuration<T> getInstance()
   {
      if (instance == null)
         return new DefaultConfiguration<T>();
      //noinspection unchecked
      return instance;
   }

   public synchronized static <T> void setInstance(Configuration<T> conf)
   {
      instance = conf;
   }

   public Class<T> getProxyClass()
   {
      if (proxyClass == null)
         throw new IllegalArgumentException("Null proxy class");
      return proxyClass;
   }

   public void setProxyClass(Class<T> proxyClass)
   {
      this.proxyClass = proxyClass;
   }

   public String getHostName()
   {
      return hostName;
   }
   
   public void setHostName(String hostName)
   {
      this.hostName = hostName;
   }
   
   public int getPort()
   {
      return port;
   }
   
   public void setPort(int port)
   {
      this.port = port;
   }
   
   public int getSslPort()
   {
      return sslPort;
   }
   
   public void setSslPort(int sslPort)
   {
      this.sslPort = sslPort;
   }

   public boolean isDebugMode()
   {
      return isDebugMode;
   }

   public void setDebugMode(boolean isDebugMode)
   {
      this.isDebugMode = isDebugMode;
   }

   public boolean isDebugLogging()
   {
      return isDebugLogging;
   }

   public void setDebugLogging(boolean isDebugLogging)
   {
      this.isDebugLogging = isDebugLogging;
   }

   public boolean isStrictSSL()
   {
      return isStrictSSL;
   }

   public void setStrictSSL(boolean strictSSL)
   {
      isStrictSSL = strictSSL;
   }
}
