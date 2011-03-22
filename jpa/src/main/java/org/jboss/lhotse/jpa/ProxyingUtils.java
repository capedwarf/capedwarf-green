package org.jboss.lhotse.jpa;

/**
 * Proxying utils.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ProxyingUtils
{
   private static ThreadLocal<Object> proxyingTL = new ThreadLocal<Object>();

   /**
    * Enable proxying.
    */
   public static void enable()
   {
      proxyingTL.remove();
   }

   /**
    * Disable proxying.
    */
   public static void disable()
   {
      proxyingTL.set(ProxyingUtils.class);
   }

   /**
    * Is proxying disabled.
    *
    * @return true if disabled, false otherwise
    */
   public static boolean isDisabled()
   {
      return (proxyingTL.get() != null);  
   }
}
