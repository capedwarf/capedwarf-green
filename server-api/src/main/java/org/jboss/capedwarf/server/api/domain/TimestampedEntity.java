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

package org.jboss.capedwarf.server.api.domain;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

/**
 * Keep track of timestamp and expiration.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
// @Entity // TODO -- re-check this
@MappedSuperclass
public abstract class TimestampedEntity extends AbstractEntity implements TimestampAware
{
   private static long serialVersionUID = 3l;

   private long timestamp;
   private long expirationTime;

   public TimestampedEntity()
   {
      super();
   }

   @Basic
   public long getTimestamp()
   {
      return timestamp;
   }

   public void setTimestamp(long timestamp)
   {
      this.timestamp = timestamp;
   }

   @Basic
   public long getExpirationTime()
   {
      return expirationTime;
   }

   public void setExpirationTime(long expirationTime)
   {
      this.expirationTime = expirationTime;
   }

   @Transient
   public String getInfo()
   {
      StringBuilder builder = new StringBuilder();
      addInfo(builder);
      return builder.toString();
   }

   protected void addInfo(StringBuilder builder)
   {
      builder.append("timestamp=").append(timestamp);
   }
}

