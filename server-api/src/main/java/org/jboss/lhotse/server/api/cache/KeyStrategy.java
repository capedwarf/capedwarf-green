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

package org.jboss.lhotse.server.api.cache;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Cache key strategy.
 *
 * @param <T> the exact cached value type
 * @param <U> the exact original value type
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface KeyStrategy<T, U>
{
   /**
    * Create cache key.
    *
    * @param target the target
    * @param method the intercepted method
    * @param args the args
    * @return cache key
    */
   Serializable createKey(Object target, Method method, Object[] args);

   /**
    * Wrap the original value.
    *
    * @param orginal the original value
    * @param target the target
    * @param method the intercepted method
    * @param args the args
    * @return wrapped value
    */
   T wrap(U orginal, Object target, Method method, Object[] args);

   /**
    * Unwrap cached value.
    *
    * @param cached the cached value
    * @param target the target
    * @param method the intercepted method
    * @param args the args
    * @return unwrapped value
    */
   U unwrap(T cached, Object target, Method method, Object[] args);
}
