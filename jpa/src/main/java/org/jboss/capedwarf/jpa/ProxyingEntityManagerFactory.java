/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.capedwarf.jpa;

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
