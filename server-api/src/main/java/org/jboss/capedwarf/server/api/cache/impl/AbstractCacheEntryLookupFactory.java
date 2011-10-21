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

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.cache.Cache;

import org.jboss.capedwarf.server.api.cache.CacheEntryLookup;
import org.jboss.capedwarf.server.api.cache.CacheEntryLookupFactory;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractCacheEntryLookupFactory implements CacheEntryLookupFactory
{
   private Map<String, CacheEntryLookup> lookups = new ConcurrentSkipListMap<String, CacheEntryLookup>();

   public CacheEntryLookup createCacheEntryLookup(String cacheName, Cache cache)
   {
      CacheEntryLookup cel = lookups.get(cacheName);
      if (cel != null)
         return cel;

      CacheEntryLookup acel = doCreateCacheEntryLookup(cache);
      lookups.put(cacheName, acel);
      return acel;
   }

   protected abstract CacheEntryLookup doCreateCacheEntryLookup(Cache cache);
}
