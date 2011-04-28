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

package org.jboss.lhotse.server.jee.tx;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.jboss.lhotse.jpa.EntityManagerProvider;
import org.jboss.lhotse.jpa2.NewProxyingEntityManager;
import org.jboss.lhotse.server.api.lifecycle.AfterImpl;
import org.jboss.lhotse.server.api.lifecycle.Notification;
import org.jboss.lhotse.server.api.persistence.EMFNotification;
import org.jboss.lhotse.server.api.persistence.EMInjector;

/**
 * JEE EM injector.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JeeEMInjector implements EMInjector, Serializable
{
   @PersistenceContext private transient EntityManager em;
   @PersistenceUnit private transient EntityManagerFactory emf;

   private transient Event<Notification<EntityManagerFactory>> produceEvent;
   private static volatile boolean emitted;

   @PostConstruct
   public void init()
   {
      // send this only once
      if (emitted == false)
      {
         emitted = true;
         produceEvent.select(new AfterImpl()).fire(new EMFNotification(emf));
      }
   }

   public EntityManager getEM()
   {
      return new NewProxyingEntityManager(em)
      {
         protected EntityManagerProvider getProvider()
         {
            return new EntityManagerProvider()
            {
               public EntityManager getEntityManager()
               {
                  return em;
               }

               public void close(EntityManager em)
               {
               }
            };
         }
      };
   }

   @Inject
   public void setProduceEvent(Event<Notification<EntityManagerFactory>> produceEvent)
   {
      this.produceEvent = produceEvent;
   }
}
