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

package org.jboss.capedwarf.server.jee.persistence;

import java.io.IOException;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.jboss.capedwarf.server.api.persistence.StatelessAdapter;
import org.jboss.capedwarf.server.api.persistence.StatelessAdapterFactory;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class HibernateStatelessAdapterFactory implements StatelessAdapterFactory
{
   public StatelessAdapter createStatelessAdapter(EntityManager em)
   {
      if (em == null)
         throw new IllegalArgumentException("Null EntityManager!");

      Object delegate = em.getDelegate();
      if (delegate instanceof Session == false)
         throw new IllegalArgumentException("Can only handle Hibernate Session: " + delegate);

      Session session = (Session) delegate;
      SessionFactory factory = session.getSessionFactory();
      return new HibernateStatelessAdapter(factory.openStatelessSession());
   }

   private static class HibernateStatelessAdapter implements StatelessAdapter
   {
      private final StatelessSession session;

      private HibernateStatelessAdapter(StatelessSession session)
      {
         this.session = session;
      }

      public Long insert(Object entity)
      {
         return (Long) session.insert(entity);
      }

      public void update(Object entity)
      {
         session.update(entity);
      }

      public void delete(Object entity)
      {
         session.delete(entity);
      }

      public <T> T get(Class<T> entityClass, Serializable id)
      {
         if (entityClass == null)
            throw new IllegalArgumentException("Null entity class!");

         return entityClass.cast(session.get(entityClass, id));
      }

      public void refresh(Object entity)
      {
         session.refresh(entity);
      }

      public void close() throws IOException
      {
         session.close();
      }
   }
}
