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

package org.jboss.lhotse.server.api.cache;

import javax.cache.Cache;
import javax.cache.CacheException;

/**
 * Cache config.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface CacheConfig
{
   /**
    * Find the cache.
    *
    * @param name the name
    * @return cache or null if no such cache
    */
   Cache findCache(String name);

   /**
    * Configure the cache.
    * If such cache already exists we ignore the config.
    *
    * @param name the cache name
    * @return cache instance, existing or new
    * @throws CacheException for any error
    */
   Cache configureCache(String name) throws CacheException;

   /**
    * Evict cache.
    *
    * @param name the cache name
    * @return true if such cache exists and was successfully evicted
    */
   boolean evictCache(String name);

   /**
    * Clear cache.
    *
    * @param name the cache name
    * @return true if such cache exists and was successfully cleared
    */
   boolean clearCache(String name);

   /**
    * Dispose cache.
    *
    * @param cache the cache to dispose
    */
   void disposeCache(Cache cache);
}
