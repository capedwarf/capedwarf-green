/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.capedwarf.server.gae.cache;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

import org.jboss.capedwarf.server.api.cache.impl.AbstractCacheEntryLookup;

/**
 * DataNucleus OID CEL.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class OIDCacheEntryLookup extends AbstractCacheEntryLookup
{
   private static final Logger log = Logger.getLogger(OIDCacheEntryLookup.class.getName());

   private String oidClassName = "org.datanucleus.identity.OIDImpl";
   private volatile Class<?> oidClass;

   protected Object toImplementationId(Class<?> entryType, Object id)
   {
      if (oidClass == null)
      {
         synchronized (this)
         {
            if (oidClass == null)
            {
               if (cache == null)
               {
                  oidClass = Void.class;
                  log.warning("Cache is null, forgot to set it?");
                  return null;
               }

               try
               {
                  oidClass = getClass().getClassLoader().loadClass(oidClassName);
               }
               catch (ClassNotFoundException e)
               {
                  log.warning("Cannot create OID: " + e);
                  oidClass = Void.class;
               }
            }
         }
      }

      // it failed
      if (oidClass == Void.class)
         return null;

      try
      {
         Constructor<?> ctor = oidClass.getConstructor(String.class, Object.class);
         return ctor.newInstance(entryType.getName(), id);
      }
      catch (Exception e)
      {
         log.fine("Cannot create OID: " + e);
         return null;
      }
   }

   public void setOidClassName(String oidClassName)
   {
      this.oidClassName = oidClassName;
   }
}
