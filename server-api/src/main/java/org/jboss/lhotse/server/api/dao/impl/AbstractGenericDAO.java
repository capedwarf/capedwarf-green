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

package org.jboss.lhotse.server.api.dao.impl;

import org.jboss.lhotse.jpa.ProxyingEnum;
import org.jboss.lhotse.server.api.dao.GenericDAO;
import org.jboss.lhotse.server.api.domain.AbstractEntity;
import org.jboss.lhotse.server.api.persistence.EMInjector;
import org.jboss.lhotse.server.api.persistence.Proxying;
import org.jboss.lhotse.server.api.tx.TransactionPropagationType;
import org.jboss.lhotse.server.api.tx.Transactional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

/**
 * Abstract generic DAO.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Transactional
@ApplicationScoped
public abstract class AbstractGenericDAO<T extends AbstractEntity> implements GenericDAO<T>
{
   private EMInjector emInjector;

   @Inject
   public void setEmInjector(EMInjector emInjector)
   {
      this.emInjector = emInjector;
   }

   protected abstract Class<T> entityClass();

   protected EntityManager getEM()
   {
      return emInjector.getEM();
   }

   @SuppressWarnings({"unchecked"})
   protected T getSingleResult(Query query)
   {
      List result = query.getResultList();
      return getSingleResult(result);
   }

   @SuppressWarnings({"unchecked"})
   protected T getSingleResult(Collection result)
   {
      return (result.isEmpty()) ? null : (T) result.iterator().next();
   }

   @SuppressWarnings({"unchecked"})
   protected Long getSingleId(Query query)
   {
      List result = query.getResultList();
      return (result.isEmpty()) ? null : ((Number) result.get(0)).longValue();
   }

   @SuppressWarnings({"unchecked"})
   protected String getSingleString(Query query)
   {
      List result = query.getResultList();
      return (result.isEmpty()) ? null : (result.get(0)).toString();
   }

   @SuppressWarnings({"unchecked"})
   protected Long getCount(Query query)
   {
      Object result = query.getSingleResult();
      return ((Number)result).longValue();
   }

   protected boolean idExists(Query query)
   {
      return exists(getSingleId(query));
   }

   protected boolean exists(Object value)
   {
      return (value != null);
   }

   public void save(T entity)
   {
      if (entity == null)
         throw new IllegalArgumentException("Null entity");

      saveInternal(entity);
   }

   protected void saveInternal(T entity)
   {
      getEM().persist(entity);
   }

   public void merge(T entity)
   {
      if (entity == null)
         throw new IllegalArgumentException("Null entity");

      getEM().merge(entity);
   }

   @Proxying(ProxyingEnum.DISABLE)
   public int delete(Long id)
   {
      if (id == null || id <= 0)
         throw new IllegalArgumentException("Illegal id: " + id);

      Query query = getEM().createQuery("delete from " + entityClass().getSimpleName() + " e where e.id = :eid");
      query.setParameter("eid", id);
      return query.executeUpdate();
   }

   public void delete(T entity)
   {
      if (entity == null)
         throw new IllegalArgumentException("Null entity");

      getEM().remove(entity);      
   }

   @Transactional(TransactionPropagationType.SUPPORTS)
   public T find(Long id)
   {
      return find(entityClass(), id);
   }

   @Transactional(TransactionPropagationType.SUPPORTS)
   public <U> U find(Class<U> clazz, Long id)
   {
      if (clazz == null)
         throw new IllegalArgumentException("Null clazz");
      if (id == null)
         throw new IllegalArgumentException("Null id");

      return getEM().find(clazz, id);
   }

   @SuppressWarnings({"unchecked"})
   @Transactional(TransactionPropagationType.SUPPORTS)
   public List<T> findAll()
   {
      EntityManager em = getEM();
      Query query = em.createQuery("select e from " + entityClass().getSimpleName() + " e");
      return query.getResultList();
   }
}
