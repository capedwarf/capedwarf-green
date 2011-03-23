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

package org.jboss.lhotse.server.api.dao.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jboss.lhotse.server.api.domain.TimestampedEntity;

/**
 * Timestamped list cache result.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class TimestampedListCachedResult implements Serializable
{
   private static final long serialVersionUID = 1l;

   private long timestamp;
   private List<? extends TimestampedEntity> results;

   public TimestampedListCachedResult(long timestamp, List<? extends TimestampedEntity> results)
   {
      if (results == null)
         results = Collections.emptyList();

      this.timestamp = timestamp;
      this.results = results;
   }

   /**
    * Get proper entity sublist based on timestamp.
    *
    * @param ts the timestamp
    * @return sublist
    */
   List<? extends TimestampedEntity> getSubList(long ts)
   {
      if (ts < timestamp)
         return null; // we need to get new results

      int size = results.size();

      if (size == 0)
         return Collections.emptyList();

      long lastTs = results.get(size - 1).getTimestamp();
      if (ts > lastTs)
         return Collections.emptyList();

      for (int i = 0; i < size; i++)
      {
         TimestampedEntity te = results.get(i);
         if (te.getTimestamp() > ts)
            return results.subList(i, size);
      }

      return Collections.emptyList();
   }
}

