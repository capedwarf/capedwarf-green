package org.jboss.lhotse.jpa;

/**
 * Create proxy for entity.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ProxyingFactory
{
   /**
    * Create proxy.
    *
    * @param entityClass the entity class
    * @return new prpoxy instance, if entity is proxyable
    * @throws Exception for any error
    */
   <T extends Entity> T createProxy(Class<T> entityClass) throws Exception;

   /**
    * Is the instance proxy.
    *
    * @param entity the entity
    * @return true if entity is proxy, false otherwise
    */
   boolean isProxy(Entity entity);
}
