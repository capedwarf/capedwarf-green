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

package org.jboss.capedwarf.server.api.cache;

import javax.cache.Cache;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Cache interceptor.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Cacheable
@Interceptor
public class CacheInterceptor implements Serializable
{
   private static final long serialVersionUID = 1l;
   private static final Logger log = Logger.getLogger(CacheInterceptor.class.getName());

   /** The keys */
   private static Map<Class<? extends KeyStrategy>, KeyStrategy> keys = new ConcurrentHashMap<Class<? extends KeyStrategy>, KeyStrategy>();

   /** The cache config */
   private transient CacheConfig cacheConfig;

   /** The cache execption handler */
   private transient CacheExceptionHandler exceptionHandler;

   @AroundInvoke
   @SuppressWarnings("unchecked")
   public Object manageCache(InvocationContext ctx) throws Exception
   {
      Class<?> clazz = ctx.getTarget().getClass();

      Method m = ctx.getMethod();
      Cacheable cacheable = m.getAnnotation(Cacheable.class);
      if (cacheable == null)
         cacheable = clazz.getAnnotation(Cacheable.class);

      // sanity check
      if (cacheable == null)
         throw new IllegalArgumentException("Null cachable, invalid usage?");

      String cacheName = cacheable.name();
      Cache cache = cacheConfig.configureCache(cacheName);

      CacheMode mode = cacheable.mode(); // should not be null, as we got intercepted

      Class<? extends KeyStrategy> ksClass = cacheable.key();
      KeyStrategy ks = keys.get(ksClass);
      if (ks == null)
      {
         try
         {
            ks = ksClass.newInstance();
         }
         catch (Exception e)
         {
            log.fine("Error creating KeyStrategy: " + e);

            Constructor<? extends KeyStrategy> ctor = ksClass.getConstructor(CacheConfig.class);
            ks = ctor.newInstance(cacheConfig);
         }
         keys.put(ksClass, ks);
      }

      Object target = ctx.getTarget();
      Object[] args = ctx.getParameters();
      Serializable key = ks.createKey(target, m, args);

      Object value = null;

      try
      {
         if (mode == CacheMode.READ_ONLY || mode == CacheMode.ALL)
            value = cache.get(key);

         if (value != null)
         {
            Object unwraped = ks.unwrap(value, target, m, args);
            if (unwraped != null) // could be invalidated
               return unwraped;
         }

         value = ctx.proceed();

         if (value != null && (mode == CacheMode.WRITE_ONLY || mode == CacheMode.ALL))
            cache.put(key, ks.wrap(value, target, m, args));
         else if (mode == CacheMode.REMOVE)
            cache.remove(key);
         else if (mode == CacheMode.EVICT)
            cache.evict();
         else if (mode == CacheMode.CLEAR)
            cache.clear();

         return value;
      }
      catch (Throwable e)
      {
         return getExceptionHandler().handleException(cache, ctx, key, value, e);
      }
   }

   protected CacheExceptionHandler getExceptionHandler()
   {
      if (exceptionHandler == null)
         exceptionHandler = cacheConfig.getExceptionHandler();

      return exceptionHandler;
   }

   @Inject
   public void setCacheConfig(CacheConfig cacheConfig)
   {
      this.cacheConfig = cacheConfig;
   }
}
