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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.kohsuke.MetaInfServices;

/**
 * Infinispan javax.cache factory implementation.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@MetaInfServices
public class InfinispanCacheFactory implements CacheFactory
{
   private static Logger log = Logger.getLogger(InfinispanCacheFactory.class.getName());
   private static String[] defaultJndiNames = {"java:jboss/infinispan/lhotse", "java:CacheManager/lhotse"};
   private EmbeddedCacheManager cacheManager;

   public InfinispanCacheFactory() throws IOException
   {
      try
      {
         cacheManager = doJNDILookup();
      }
      catch (Throwable t)
      {
         log.warning("Failed to do JNDI lookup, using standalone configuration: " + t);
         cacheManager = doStandalone();
      }
   }

   protected EmbeddedCacheManager doJNDILookup() throws IOException
   {
      Properties jndiProperties = new Properties();
      URL jndiPropertiesURL = getClass().getClassLoader().getResource("jndi.properties");
      if (jndiPropertiesURL != null)
      {
         InputStream is = jndiPropertiesURL.openStream();
         try
         {
            jndiProperties.load(is);
         }
         finally
         {
            try
            {
               is.close();
            }
            catch (IOException ignored)
            {
            }
         }
      }

      String jndiNamespace = jndiProperties.getProperty("infinispan.jndi.name");
      Context ctx = null;
      try
      {
         ctx = new InitialContext(jndiProperties);

         EmbeddedCacheManager manager;
         if (jndiNamespace != null)
            manager = (EmbeddedCacheManager) ctx.lookup(jndiNamespace);
         else
            manager = checkDefaultNames(ctx);

         log.info("Using JNDI found CacheManager: " + manager);
         return manager;
      }
      catch (NamingException ne)
      {
         String msg = "Unable to retrieve CacheManager from JNDI [" + jndiNamespace + "]";
         log.info(msg + ": " + ne);
         throw new IOException(msg);
      }
      finally
      {
         if (ctx != null)
         {
            try
            {
               ctx.close();
            }
            catch (NamingException ne)
            {
               log.info("Unable to release initial context: " + ne);
            }
         }
      }
   }

   protected EmbeddedCacheManager checkDefaultNames(Context ctx) throws IOException
   {
      for (String jndiName : defaultJndiNames)
      {
         try
         {
            return (EmbeddedCacheManager) ctx.lookup(jndiName);
         }
         catch (NamingException ne)
         {
            String msg = "Unable to retrieve CacheManager from JNDI [" + jndiName + "]";
            log.fine(msg + ": " + ne);
         }
      }
      throw new IOException("Cannot find default JNDI cache manager: " + Arrays.toString(defaultJndiNames));
   }

   protected EmbeddedCacheManager doStandalone() throws IOException
   {
      String configurationFile = System.getProperty("org.jboss.lhotse.cache.configurationFile", "infinispan-config.xml");
      return new DefaultCacheManager(configurationFile, true);
   }

   public Cache createCache(Map map) throws CacheException
   {
      String cacheName = (String) map.get("cache-name");
      org.infinispan.Cache cache = cacheManager.getCache(cacheName);
      return new InfinispanCache(cache);
   }
}
