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

package org.jboss.lhotse.server.api.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.lhotse.jpa.EntityManagerProvider;
import org.jboss.lhotse.jpa.ProxyingFactory;
import org.jboss.lhotse.jpa.ProxyingWrapper;
import org.jboss.lhotse.server.api.lifecycle.AfterImpl;
import org.jboss.lhotse.server.api.lifecycle.BeforeImpl;
import org.jboss.lhotse.server.api.lifecycle.Notification;

/**
 * EntityManagerFactory provider.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class EMF
{
   private volatile EntityManagerFactory emf;
   private Event<Notification<EntityManagerFactory>> produceEvent;

   @Produces
   @ApplicationScoped
   public EntityManagerFactory produceFactory(EMFInfo info)
   {
      produceEvent.select(new BeforeImpl()).fire(new EMFNotification(null)); // let app know we're about to create EMF
      EntityManagerFactory entityManagerFactory = getFactory(info);
      produceEvent.select(new AfterImpl()).fire(new EMFNotification(entityManagerFactory)); // let app know we created EMF
      return entityManagerFactory;
   }

   @Produces
   @ApplicationScoped
   public ProxyingFactory produceProxyingFactory(EMFInfo info)
   {
      return (ProxyingFactory) getFactory(info);
   }

   private EntityManagerFactory getFactory(EMFInfo info)
   {
      if (emf == null)
      {
         synchronized (this)
         {
            if (emf == null)
            {
               EntityManagerFactory delegate = new LazyEntityManagerFactory(info.getUnitName());
               EntityManagerProvider provider = new CurrentEntityManagerProvider(delegate, info.getEmInjector());
               ProxyingWrapper wrapper = info.getWrapper();
               emf = wrapper.wrap(delegate, provider);
            }
         }
      }      
      return emf;
   }

   @Inject
   public void setProduceEvent(Event<Notification<EntityManagerFactory>> produceEvent)
   {
      this.produceEvent = produceEvent;
   }

   private static class CurrentEntityManagerProvider implements EntityManagerProvider
   {
      private EntityManagerFactory emf;
      private EMInjector emInjector;

      private CurrentEntityManagerProvider(EntityManagerFactory emf, EMInjector emInjector)
      {
         this.emf = emf;
         this.emInjector = emInjector;
      }

      public EntityManager getEntityManager()
      {
         EntityManager em = emInjector.getEM();
         return (em != null) ? em : emf.createEntityManager();
      }

      public void close(EntityManager em)
      {
         EntityManager tmp = emInjector.getEM();
         if (tmp != em)
            em.close();
      }
   }
}
