package org.jboss.capedwarf.server.api.cache.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.inject.Inject;

import org.jboss.capedwarf.server.api.cache.CacheConfig;
import org.jboss.capedwarf.server.api.cache.CacheEntryLookup;
import org.jboss.seam.solder.resourceLoader.Resource;

/**
 * Cache config impl.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractCacheConfig implements CacheConfig
{
   protected Logger log = Logger.getLogger(getClass().getName());
   private Properties props;

   private CacheManager manager;
   private Map<String, CacheEntryLookup> lookups = new ConcurrentHashMap<String, CacheEntryLookup>();

   /**
    * Create config.
    *
    * @param name the cache name
    * @return config map
    */
   protected abstract Map createConfig(String name);

   public Cache findCache(String name)
   {
      return manager.getCache(name);
   }

   @SuppressWarnings({"unchecked"})
   public Cache configureCache(String name) throws CacheException
   {
      Cache cache = manager.getCache(name);
      if (cache != null)
         return cache;

      Map config = createConfig(name);
      Map wrappedConfig = new HashMap(config);
      wrappedConfig.put("cache-name", name);
      CacheFactory factory = manager.getCacheFactory();
      cache = factory.createCache(wrappedConfig);
      manager.registerCache(name, cache);
      return cache;
   }

   public boolean evictCache(String name)
   {
      Cache cache = manager.getCache(name);
      if (cache != null)
      {
         cache.evict();
         return true;
      }
      return false;
   }

   public boolean clearCache(String name)
   {
      Cache cache = manager.getCache(name);
      if (cache != null)
      {
         cache.clear();
         return true;
      }
      return false;
   }

   public void disposeCache(Cache cache)
   {
      // do nothing by default
   }

   public CacheEntryLookup getLookup(String cacheName) throws CacheException
   {
      CacheEntryLookup cel = lookups.get(cacheName);
      if (cel != null)
         return cel;

      AbstractCacheEntryLookup acel = createLookup();
      acel.setCache(configureCache(cacheName));
      lookups.put(cacheName, acel);
      return acel;
   }

   protected abstract AbstractCacheEntryLookup createLookup();

   @Inject
   public void setManager(CacheManager manager)
   {
      this.manager = manager;
   }

   protected Properties getProps()
   {
      return props;
   }

   @Inject
   public void setProps(@Resource("cache.properties") Properties props)
   {
      this.props = props;
   }
}
