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

package org.jboss.lhotse.server.api.dao;

import java.util.List;

import org.jboss.lhotse.server.api.domain.AbstractEntity;

/**
 * Generic DAO.
 *
 * @param <T> exact dao type
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface GenericDAO<T extends AbstractEntity>
{
   /**
    * Save entity.
    *
    * @param entity the entity
    */
   void save(T entity);

   /**
    * Merge entity.
    *
    * @param entity the entity
    */
   void merge(T entity);

   /**
    * Delete entity.
    *
    * @param id the entity id
    * @return 1 if deletion was performed, 0 otherwise
    */
   int delete(Long id);

   /**
    * Delete entity.
    *
    * @param entity the entity
    */
   void delete(T entity);

   /**
    * Find entity.
    *
    * @param id the entity id
    * @return found entity or null
    */
   T find(Long id);

   /**
    * Find entity.
    *
    * @param clazz the entity class
    * @param id the entity id
    * @return found entity or null
    */
   <U> U find(Class<U> clazz, Long id);

   /**
    * Find all entities.
    *
    * @return all entities
    */
   List<T> findAll();
}
