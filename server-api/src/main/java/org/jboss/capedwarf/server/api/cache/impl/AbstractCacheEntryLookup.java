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

package org.jboss.capedwarf.server.api.cache.impl;

import javax.cache.Cache;

import org.jboss.capedwarf.server.api.cache.CacheEntryLookup;

/**
 * Abstract CEL.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractCacheEntryLookup implements CacheEntryLookup
{
   protected Cache cache;

   protected abstract Object toImplementationId(Class<?> entryType, Object id);

   public <T> T getCachedEntry(Class<T> entryType, Object id)
   {
      if (entryType == null || id == null)
         return null;

      Object oid = toImplementationId(entryType, id);
      if (oid == null)
         return null;

      Object result = cache.get(oid);
      return entryType.cast(result);
   }

   public void setCache(Cache cache)
   {
      this.cache = cache;
   }
}
