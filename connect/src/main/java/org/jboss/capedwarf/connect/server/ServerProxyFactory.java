package org.jboss.capedwarf.connect.server;

import org.jboss.capedwarf.connect.config.Configuration;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.WeakHashMap;

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
   private static Map<Class<?>, ServerProxyHandle> instances = new WeakHashMap<Class<?>, ServerProxyHandle>();

   /**
    * Create ServerProxy proxy.
    *
    * @param proxyClass the proxy class
    * @return the ServerProxy proxy
    */
   @SuppressWarnings({"unchecked"})
   public synchronized static <T> T create(Class<T> proxyClass)
   {
      T instance = (T) instances.get(proxyClass);
      if (instance == null)
      {
         Configuration<T> config = Configuration.getInstance();
         config.setProxyClass(proxyClass);
         instance = create(config);
         instances.put(proxyClass, (ServerProxyHandle) instance);
      }
      return instance;
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
    * @param handler    the server proxy invocation handler
    * @param proxyClass the proxy class
    * @return the ServerProxy proxy
    */
   @SuppressWarnings({"unchecked"})
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
    *
    * @param proxyClass the proxy class
    */
   public synchronized static void shutdown(Class<?> proxyClass)
   {
      ServerProxyHandle handle = instances.remove(proxyClass);
      if (handle != null)
      {
         handle.shutdown();
      }
   }
}
