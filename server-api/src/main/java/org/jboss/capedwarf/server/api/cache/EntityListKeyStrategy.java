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

package org.jboss.capedwarf.server.api.cache;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.cache.CacheException;

import org.jboss.capedwarf.jpa.Entity;

/**
 * Cache only entity ids.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class EntityListKeyStrategy<T extends EntityListCachedResult, E extends Entity> implements KeyStrategy<T, List<E>>
{
   private CacheEntryLookup lookup;

   protected EntityListKeyStrategy(CacheConfig config) throws CacheException
   {
      if (config == null)
         throw new IllegalArgumentException("Null cache config");
      lookup = config.getLookup(null);
   }

   /**
    * Get exact entity class.
    *
    * @return the exact entity class
    */
   protected abstract Class<E> getEntityClass();

   public List<E> unwrap(T cached, Object target, Method method, Object[] args)
   {
      List<Long> ids = cached.getIds();
      return getEntities(ids);
   }

   /**
    * Get entities.
    *
    * @param ids the entity ids
    * @return actual entities
    */
   protected List<E> getEntities(List<Long> ids)
   {
      if (ids == null)
         return null; // somebody invalidated ids?

      List<E> entities = new ArrayList<E>();
      for (Long id : ids)
      {
         E entity = lookup.getCachedEntry(getEntityClass(), id);
         if (entity == null) // we cannot re-create the whole cached list
            return null;

         entities.add(entity);
      }
      return entities;
   }
}
