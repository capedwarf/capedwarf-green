package org.jboss.lhotse.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.util.Map;

/**
 * Proxy wrapping EntityManagerFactory.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ProxyingEntityManagerFactory implements EntityManagerFactory, ProxyingFactory
{
   private EntityManagerFactory delegate;
   private EntityManagerProvider provider;
   private ProxyingFactory factory;

   public ProxyingEntityManagerFactory(EntityManagerFactory delegate)
   {
      this(delegate, new NewEntityManagerProvider(delegate));
   }

   public ProxyingEntityManagerFactory(EntityManagerFactory delegate, EntityManagerProvider provider)
   {
      if (delegate == null)
         throw new IllegalArgumentException("Null delegate");
      if (provider == null)
         throw new IllegalArgumentException("Null provider");

      this.delegate = delegate;
      this.provider = provider;
      this.factory = new ProxyingHelper()
      {
         protected EntityManagerProvider getProvider()
         {
            return ProxyingEntityManagerFactory.this.provider;
         }
      };
   }

   /**
    * Get EM provider internal.
    *
    * @return the EM provider
    */
   protected EntityManagerProvider getProviderInternal()
   {
      return provider;
   }

   protected EntityManager proxy(final EntityManager delegate)
   {
      return new ProxyingEntityManager(delegate)
      {
         protected EntityManagerProvider getProvider()
         {
            return getProviderInternal();
         }
      };
   }

   public <T extends Entity> T createProxy(Class<T> entityClass) throws Exception
   {
      return factory.createProxy(entityClass);
   }

   public boolean isProxy(Entity entity)
   {
      return factory.isProxy(entity);
   }

   public EntityManager createEntityManager()
   {
      EntityManager em = delegate.createEntityManager();
      return proxy(em);
   }

   public EntityManager createEntityManager(Map map)
   {
      EntityManager em = delegate.createEntityManager(map);
      return proxy(em);
   }

   public void close()
   {
      delegate.close();
   }

   public boolean isOpen()
   {
      return delegate.isOpen();
   }

   private static class NewEntityManagerProvider implements EntityManagerProvider
   {
      private EntityManagerFactory emf;

      private NewEntityManagerProvider(EntityManagerFactory emf)
      {
         if (emf == null)
            throw new IllegalArgumentException("Null emf");
         this.emf = emf;
      }

      public EntityManager getEntityManager()
      {
         return emf.createEntityManager();
      }

      public void close(EntityManager em)
      {
         em.close();
      }
   }
}
