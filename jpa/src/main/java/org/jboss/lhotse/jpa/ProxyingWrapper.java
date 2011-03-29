package org.jboss.lhotse.jpa;

import javax.persistence.EntityManagerFactory;

/**
 * Wrap EMF with proxying.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ProxyingWrapper
{
   /**
    * Wrap EMF delegate.
    *
    * @param delegate the EMF delegate
    * @param provider the EM provider
    * @return wrapped delegate
    */
   EntityManagerFactory wrap(EntityManagerFactory delegate, EntityManagerProvider provider);
}
