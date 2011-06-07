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

package org.jboss.capedwarf.server.api.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import javax.cache.Cache;
import javax.cache.CacheStatistics;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.capedwarf.server.api.cache.CacheConfig;
import org.jboss.capedwarf.server.api.security.Security;

/**
 * Cache command.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Named("cc")
@ConversationScoped
public class CacheCommand extends Command implements Serializable
{
   private static final long serialVersionUID = 1l;

   private transient CacheConfig cacheConfig;
   private List<CacheEntry> entries = Collections.emptyList();

   public List<CacheEntry> getEntries()
   {
      return entries;
   }

   public int getSize()
   {
      return entries.size();
   }

   protected void display(Object key, Object value)
   {
      entries = Collections.singletonList(new CacheEntry(key, value));
   }

   protected String getName(String form)
   {
      return getParameter(form, "cacheName");
   }

   @Security
   public void evictCache()
   {
      String name = getName("evict");
      display("Evict: " + name, cacheConfig.evictCache(name));
   }

   @Security
   public void clearCache()
   {
      String name = getName("clear");
      display("Clear: " + name, cacheConfig.clearCache(name));
   }

   @Security
   public void executeCache()
   {
      String execute = getParameter("execute", "args");
      String[] split = execute.split(",");
      if (split.length != 3)
         throw new IllegalArgumentException("Illegal execute args: " + execute);

      String name = split[0];
      Cache cache = cacheConfig.findCache(name);
      if (cache != null)
      {
         String op = split[1];
         Object key;
         try
         {
            key = Long.parseLong(split[2]);
         }
         catch (Throwable ignored)
         {
            key = split[2];
         }

         // ops
         if ("get".equals(op))
         {
            display(key, cache.get(key));
         }
         else if ("getCacheEntry".equals(op))
         {
            display(key, cache.getCacheEntry(key));
         }
         else if ("peek".equals(op))
         {
            display(key, cache.peek(key));
         }
         else if ("remove".equals(op))
         {
            display(key, cache.remove(key));
         }
         else if ("size".equals(op))
         {
            display("Size: ", cache.size());
         }
      }
      else
      {
         display("", "No such cache:" + name);
      }
   }

   @Security
   public void statsCache() throws Exception
   {
      String execute = getParameter("stats", "args");
      String[] split = execute.split(",");
      if (split.length != 2)
         throw new IllegalArgumentException("Illegal stats args: " + execute);

      String name = split[0];
      Cache cache = cacheConfig.findCache(name);
      if (cache != null)
      {
         CacheStatistics stats = cache.getCacheStatistics();
         String op = split[1];
         Method m = CacheStatistics.class.getMethod(op);
         display(op, m.invoke(stats));
      }
      else
      {
         display("", "No such cache:" + name);
      }
   }

   @Inject
   public void setCacheConfig(CacheConfig cacheConfig)
   {
      this.cacheConfig = cacheConfig;
   }
}
