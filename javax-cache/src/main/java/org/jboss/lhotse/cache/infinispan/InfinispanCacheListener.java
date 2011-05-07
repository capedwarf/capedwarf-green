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

import javax.cache.CacheListener;

import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryEvicted;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryLoaded;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryCreatedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryEvictedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryLoadedEvent;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;

/**
 * Infinispan javax.cache listener.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@org.infinispan.notifications.Listener
public class InfinispanCacheListener
{
   private CacheListener listener;

   InfinispanCacheListener(CacheListener listener)
   {
      this.listener = listener;
   }

   @CacheEntryEvicted
   public void onEvict(CacheEntryEvictedEvent event)
   {
      listener.onEvict(event.getKey());
   }

   @CacheEntryLoaded
   public void onLoad(CacheEntryLoadedEvent event)
   {
      listener.onLoad(event.getKey());
   }

   @CacheEntryCreated
   public void onPut(CacheEntryCreatedEvent event)
   {
      listener.onPut(event.getKey());
   }

   @CacheEntryRemoved
   public void onRemove(CacheEntryRemovedEvent event)
   {
      listener.onRemove(event.getKey());
   }

   @Override
   public int hashCode()
   {
      return listener.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      return listener.equals(obj);
   }

   @Override
   public String toString()
   {
      return listener.toString();
   }
}
