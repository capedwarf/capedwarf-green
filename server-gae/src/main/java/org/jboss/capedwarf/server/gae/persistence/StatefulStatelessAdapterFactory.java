/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.capedwarf.server.gae.persistence;

import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;

import org.jboss.capedwarf.jpa.Entity;
import org.jboss.capedwarf.server.api.persistence.AbstractStatelessAdapterFactory;
import org.jboss.capedwarf.server.api.persistence.StatelessAdapter;

/**
 * Not really stateless - simply delegates to EM.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class StatefulStatelessAdapterFactory extends AbstractStatelessAdapterFactory
{
   protected StatelessAdapter doCreateStatelessAdapter(EntityManager em)
   {
      return new EMStatelessAdapter(em);
   }

   private static class EMStatelessAdapter implements StatelessAdapter
   {
      private final EntityManager em;

      private EMStatelessAdapter(EntityManager em)
      {
         this.em = em;
      }

      public Long insert(Object entity)
      {
         if (entity instanceof Entity == false)
            throw new IllegalArgumentException("Not Entity: " + entity);

         em.persist(entity);
         em.flush();
         return ((Entity) entity).getId();
      }

      public void update(Object entity)
      {
         em.merge(entity);
         em.flush();
      }

      public void delete(Object entity)
      {
         em.remove(entity);
      }

      public <T> T get(Class<T> entityClass, Serializable id)
      {
         return em.find(entityClass, id);
      }

      public void refresh(Object entity)
      {
         em.refresh(entity);
      }

      public void close()
      {
         // nothing to close
      }

      public void initialize(Object proxy)
      {
         // dunno how to initialize
      }
   }
}
