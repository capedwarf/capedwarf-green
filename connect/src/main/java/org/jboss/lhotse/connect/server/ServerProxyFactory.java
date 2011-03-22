package org.jboss.lhotse.connect.server;

import java.lang.reflect.Proxy;

import org.jboss.lhotse.connect.config.Configuration;

/**
 * Create ServerProxy instance.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ServerProxyFactory
{
   /**
    * The proxy instance
    */
   private static ServerProxyHandle instance;

   /**
    * Create ServerProxy proxy.
    *
    * @return the ServerProxy proxy
    */
   @SuppressWarnings({"unchecked"})
   public synchronized static <T> T create()
   {
      if (instance == null)
      {
         Configuration<T> config = Configuration.getInstance();
         instance = (ServerProxyHandle) create(config);
      }

      return (T) instance;
   }

   /**
    * Create ServerProxy proxy.
    *
    * @param config the configuration
    * @return the ServerProxy proxy
    */
   public static <T> T create(Configuration<T> config)
   {
      return create(new ServerProxyHandler(config), config.getProxyClass());
   }

   /**
    * Create ServerProxy proxy.
    *
    * @param handler the server proxy invocation handler
    * @param proxyClass the proxy class
    * @return the ServerProxy proxy
    */
   public static <T> T create(ServerProxyInvocationHandler handler, Class<T> proxyClass)
   {
      Object proxy = Proxy.newProxyInstance(
            ServerProxyHandle.class.getClassLoader(),
            new Class<?>[]{ServerProxyHandle.class, proxyClass},
            handler);
      return proxyClass.cast(proxy);
   }

   /**
    * Shutdown the server proxy.
    */
   public synchronized static void shutdown()
   {
      ServerProxyHandle handle = instance;
      instance = null; // nullify
      
      if (handle != null)
      {
         handle.shutdown();
      }
   }
}
