package org.jboss.lhotse.jpa;

/**
 * Get real entity from proxy.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ProxyEntity<T>
{
   /**
    * Get real entity.
    *
    * @return the real entity
    */
   T getRealEntity();
}
