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

package org.jboss.capedwarf.server.gae.cache;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import org.jboss.capedwarf.server.api.cache.CacheEntryLookup;
import org.jboss.capedwarf.server.api.cache.impl.AbstractCacheConfig;

import javax.cache.CacheException;
import javax.enterprise.context.ApplicationScoped;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache config impl.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class CacheConfigImpl extends AbstractCacheConfig
{
   private static final Map<String, Object> CONSTS;

   static
   {
      CONSTS = new HashMap<String, Object>();
      CONSTS.put("EXPIRATION_DELTA", GCacheFactory.EXPIRATION_DELTA);
      CONSTS.put("EXPIRATION_DELTA_MILLIS", GCacheFactory.EXPIRATION_DELTA_MILLIS);
      CONSTS.put("EXPIRATION", GCacheFactory.EXPIRATION);
   }

   @SuppressWarnings({"unchecked"})
   protected Map createConfig(String name)
   {
      Map config = new HashMap();
      String prefix = name + ".";
      for (String key : getProps().stringPropertyNames())
      {
         if (key.startsWith(prefix))
         {
            String subKey = key.substring(prefix.length());
            String value = getProps().getProperty(key);
            Object c = CONSTS.get(subKey);
            if (c != null)
            {
               config.put(c, Integer.parseInt(value));
            }
            else
            {
               MemcacheService.SetPolicy policy = null;
               for (MemcacheService.SetPolicy p : MemcacheService.SetPolicy.values())
               {
                  if (p.name().equalsIgnoreCase(subKey))
                  {
                     policy = p;
                     break;
                  }
               }
               if (policy != null)
               {
                  config.put(policy, Boolean.parseBoolean(value));
               }
               else
               {
                  config.put(subKey, value); // other custom props
               }
            }
         }
      }
      return config.isEmpty() ? Collections.emptyMap() : config;
   }

   protected CacheEntryLookup createLookup(String cacheEntry) throws CacheException
   {
      DNCacheEntryLookup cel = new DNCacheEntryLookup();
      cel.setCache(configureCache(cacheEntry));
      return cel;
   }
}