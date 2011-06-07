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

package org.jboss.capedwarf.cache.infinispan;

import javax.cache.CacheStatistics;

import org.infinispan.AdvancedCache;
import org.infinispan.stats.Stats;

/**
 * Infinispan javax.cache stats.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class InfinispanCacheStatistics implements CacheStatistics
{
   private AdvancedCache cache;

   InfinispanCacheStatistics(AdvancedCache cache)
   {
      this.cache = cache;
   }

   protected Stats getStats()
   {
      return cache.getStats();
   }

   public void clearStatistics()
   {
      // TODO?
   }

   public int getCacheHits()
   {
      return new Long(getStats().getHits()).intValue();
   }

   public int getCacheMisses()
   {
      return new Long(getStats().getMisses()).intValue();
   }

   public int getObjectCount()
   {
      return getStats().getCurrentNumberOfEntries();
   }

   public int getStatisticsAccuracy()
   {
      return CacheStatistics.STATISTICS_ACCURACY_GUARANTEED;
   }
}
