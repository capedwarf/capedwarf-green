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

package org.jboss.lhotse.cache.infinispan;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import java.io.IOException;
import java.util.Map;

/**
 * Infinispan javax.cache factory implementation.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class InfinispanCacheFactory implements CacheFactory
{
   private final EmbeddedCacheManager cacheManager;

   public InfinispanCacheFactory() throws IOException
   {
      String configurationFile = System.getProperty("org.jboss.lhotse.cache.configurationFile", "infinispan-config.xml");
      cacheManager = new DefaultCacheManager(configurationFile, true);
   }

   public Cache createCache(Map map) throws CacheException
   {
      String cacheName = (String) map.get("cache-name");
      org.infinispan.Cache cache = cacheManager.getCache(cacheName);
      return new InfinispanCache(cache);
   }
}
