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

package org.jboss.capedwarf.jpa2;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

import org.jboss.capedwarf.jpa.ProxyingEntityManager;

/**
 * JPA2 proxying extity manager.
 *
 * @author Matej Lazar
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class NewProxyingEntityManager extends ProxyingEntityManager implements EntityManager
{
   private EntityManager delegate;

   public NewProxyingEntityManager(EntityManager delegate)
   {
      super(delegate);
      this.delegate = delegate;
   }

   public Metamodel getMetamodel()
   {
      return delegate.getMetamodel();
   }

   public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties)
   {
      T result = delegate.find(entityClass, primaryKey, properties);
      return safeWrap(result);
   }

   public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode)
   {
      T result = delegate.find(entityClass, primaryKey, lockMode);
      return safeWrap(result);
   }

   public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties)
   {
      T result = delegate.find(entityClass, primaryKey, lockMode, properties);
      return safeWrap(result);
   }

   public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties)
   {
      entity = getEntity(entity);
      delegate.lock(entity, lockMode, properties);
   }

   public void refresh(Object entity, Map<String, Object> properties)
   {
      entity = getEntity(entity);
      delegate.refresh(entity, properties);
   }

   public void refresh(Object entity, LockModeType lockMode)
   {
      entity = getEntity(entity);
      delegate.refresh(entity, lockMode);
   }

   public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties)
   {
      entity = getEntity(entity);
      delegate.refresh(entity, lockMode, properties);
   }

   public void detach(Object entity)
   {
      delegate.detach(getEntity(entity));
   }

   public LockModeType getLockMode(Object entity)
   {
      return delegate.getLockMode(getEntity(entity));
   }

   public void setProperty(String propertyName, Object value)
   {
      delegate.setProperty(propertyName, value);
   }

   public Map<String, Object> getProperties()
   {
      return delegate.getProperties();
   }

   @Override
   public Query createQuery(String string)
   {
      Query query = delegate.createQuery(string);
      return new NewProxyingQuery(getProvider(), query);
   }

   @Override
   public Query createNamedQuery(String string)
   {
      Query query = delegate.createNamedQuery(string);
      return new NewProxyingQuery(getProvider(), query);
   }

   @Override
   public Query createNativeQuery(String string)
   {
      Query query = delegate.createNativeQuery(string);
      return new NewProxyingQuery(getProvider(), query);
   }

   @Override
   public Query createNativeQuery(String string, Class aClass)
   {
      Query query = delegate.createNativeQuery(string, aClass);
      return new NewProxyingQuery(getProvider(), query);
   }

   @Override
   public Query createNativeQuery(String string, String s1)
   {
      Query query = delegate.createNativeQuery(string, s1);
      return new NewProxyingQuery(getProvider(), query);
   }

   public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery)
   {
      TypedQuery<T> query = delegate.createQuery(criteriaQuery);
      return new TypedProxyingQuery<T>(getProvider(), query);
   }

   public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass)
   {
      TypedQuery<T> query = delegate.createQuery(qlString, resultClass);
      return new TypedProxyingQuery<T>(getProvider(), query);
   }

   public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass)
   {
      TypedQuery<T> namedQuery = delegate.createNamedQuery(name, resultClass);
      return new TypedProxyingQuery<T>(getProvider(), namedQuery);
   }

   public <T> T unwrap(Class<T> cls)
   {
      return delegate.unwrap(cls);
   }

   public EntityManagerFactory getEntityManagerFactory()
   {
      return delegate.getEntityManagerFactory();
   }

   public CriteriaBuilder getCriteriaBuilder()
   {
      return delegate.getCriteriaBuilder();
   }
}
