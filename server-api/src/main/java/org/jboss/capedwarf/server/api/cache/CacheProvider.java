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

import org.jboss.capedwarf.server.api.qualifiers.Name;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 * Provide named cache.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class CacheProvider
{
   private CacheConfig config;

   @Produces @Name("")
   public Cache createAppCache(InjectionPoint ip) throws CacheException
   {
      return config.configureCache(ip.getAnnotated().getAnnotation(Name.class).value());
   }

   public void disposeAppCache(@Disposes @Name("") Cache cache)
   {
      config.disposeCache(cache);
   }

   @Inject
   public void setConfig(CacheConfig config)
   {
      this.config = config;
   }
}
