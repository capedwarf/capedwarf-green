package org.jboss.lhotse.jpa;

/**
 * Proxying enum.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public enum ProxyingEnum
{
   ENABLE,
   DISABLE;

   /**
    * Begin utils usage.
    */
   public void begin()
   {
      if (this == ENABLE)
         ProxyingUtils.enable();
      else
         ProxyingUtils.disable();
   }

   /**
    * End utils usage.
    */
   public void end()
   {
      if (this == ENABLE)
         ProxyingUtils.disable();
      else
         ProxyingUtils.enable();
   }
}
