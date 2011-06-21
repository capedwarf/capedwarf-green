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

package org.jboss.capedwarf.server.api.dao.impl;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;
import javax.cache.CacheException;

import org.jboss.capedwarf.server.api.cache.CacheConfig;
import org.jboss.capedwarf.server.api.cache.EntityListKeyStrategy;
import org.jboss.capedwarf.server.api.domain.TimestampedEntity;

/**
 * Timestamped list handling.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class TimestampedListKeyStrategy<E extends TimestampedEntity> extends EntityListKeyStrategy<TimestampedListCachedResult, E>
{
   protected TimestampedListKeyStrategy(CacheConfig config) throws CacheException
   {
      super(config);
   }

   protected int getKeyIndex()
   {
      return 0;
   }

   protected int getTimestampIndex()
   {
      return 1;
   }

   public Serializable createKey(Object target, Method method, Object[] args)
   {
      Prefix prefix = method.getAnnotation(Prefix.class);
      return createKey(prefix != null ? prefix.value() : null, args[getKeyIndex()]);
   }

   protected Serializable createKey(String prefix, Object arg)
   {
      return (prefix != null) ? prefix + arg : (Serializable) arg;
   }

   public TimestampedListCachedResult wrap(List<E> orginal, Object target, Method method, Object[] args)
   {
      return new TimestampedListCachedResult((Long) args[getTimestampIndex()], orginal);
   }

   public List<E> unwrap(TimestampedListCachedResult cached, Object target, Method method, Object[] args)
   {
      List<Long> ids = cached.getSubList((Long) args[getTimestampIndex()]);
      return getEntities(ids);
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.METHOD)
   public static @interface Prefix
   {
      /**
       * The prefix.
       *
       * @return the prefix
       */
      String value();
   }
}

