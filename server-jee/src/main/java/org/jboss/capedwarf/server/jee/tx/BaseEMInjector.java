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

package org.jboss.capedwarf.server.jee.tx;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.capedwarf.jpa.EntityManagerProvider;
import org.jboss.capedwarf.jpa2.NewProxyingEntityManager;
import org.jboss.capedwarf.server.api.lifecycle.AfterImpl;
import org.jboss.capedwarf.server.api.lifecycle.Notification;
import org.jboss.capedwarf.server.api.persistence.EMFNotification;
import org.jboss.capedwarf.server.api.persistence.EMInjector;

/**
 * Base JEE EM injector.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class BaseEMInjector implements EMInjector, Serializable
{
   private transient Event<Notification<EntityManagerFactory>> produceEvent;
   private static AtomicBoolean emitted = new AtomicBoolean(false);

   private transient EntityManager current;

   protected abstract EntityManager getInjectedEntityManager();

   protected abstract EntityManagerFactory getInjectedEntityManagerFactory();

   @PostConstruct
   public void init()
   {
      // fire this only once
      if (emitted.compareAndSet(false, true))
      {
         produceEvent.select(new AfterImpl()).fire(new EMFNotification(getInjectedEntityManagerFactory()));
      }
   }

   public EntityManager getEM()
   {
      if (current == null)
         current = new NewProxyingEntityManager(getInjectedEntityManager())
         {
            protected EntityManagerProvider getProvider()
            {
               return new EntityManagerProvider()
               {
                  public EntityManager getEntityManager()
                  {
                     return getInjectedEntityManager();
                  }

                  public void close(EntityManager em)
                  {
                  }
               };
            }
         };

      return current;
   }

   @Inject
   public void setProduceEvent(Event<Notification<EntityManagerFactory>> produceEvent)
   {
      this.produceEvent = produceEvent;
   }
}
