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

package org.jboss.lhotse.server.jee.cache;

import org.jboss.lhotse.server.api.cache.impl.AbstractCacheConfig;

import javax.cache.Cache;
import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * JEE cache config impl.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class CacheConfigImpl extends AbstractCacheConfig
{
   private String disposeMethodName = "stop";
   private Method disposeMethod;
   private boolean checked;

   @Override
   protected Map createConfig(String name)
   {
      return Collections.emptyMap();
   }

   @Override
   public void disposeCache(Cache cache)
   {
      if (cache == null)
         return;

      if (checked == false)
      {
         try
         {
            Class<?> clazz = cache.getClass();
            disposeMethod = clazz.getMethod(disposeMethodName);
            disposeMethod.setAccessible(true);
         }
         catch (Throwable t)
         {
            log.info("Cannot dispose cache: " + t);
         }
         checked = true;
      }

      if (disposeMethod != null)
      {
         try
         {
            disposeMethod.invoke(cache);
         }
         catch (Throwable t)
         {
            log.finest("Error disposing cache: " + t);
         }
      }
   }

   public void setDisposeMethodName(String disposeMethodName)
   {
      this.disposeMethodName = disposeMethodName;
   }
}
