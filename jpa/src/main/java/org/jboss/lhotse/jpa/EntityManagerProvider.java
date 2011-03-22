package org.jboss.lhotse.jpa;

import javax.persistence.EntityManager;

/**
 * Entity manager provider.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface EntityManagerProvider
{
   /**
    * Get entity manager.
    *
    * @return the em
    */
   EntityManager getEntityManager();

   /**
    * Close entity manager.
    *
    * @param em the entity manager
    */
   void close(EntityManager em);
}
