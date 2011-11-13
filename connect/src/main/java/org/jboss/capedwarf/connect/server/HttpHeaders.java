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

package org.jboss.capedwarf.connect.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * Set custom http headers.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class HttpHeaders
{
   private static final ThreadLocal<Set<Header>> tlh = new ThreadLocal<Set<Header>>();

   /**
    * Get headers; read-only.
    *
    * @return the headers
    */
   public static Set<Header> getHeaders()
   {
      Set<Header> set = tlh.get();
      if (set == null)
         return Collections.emptySet();

      return Collections.unmodifiableSet(set);
   }

   /**
    * Cleanup.
    */
   public static void clear()
   {
      Set<Header> set = tlh.get();
      if (set != null)
      {
         tlh.remove();
         set.clear();
      }
   }

   /**
    * Add header.
    *
    * @param name the name
    * @param value the value
    */
   public static void addHeader(String name, String value)
   {
      if (name == null)
         throw new IllegalArgumentException("Null name");
      if (value == null)
         throw new IllegalArgumentException("Null value");

      addHeader(new BasicHeader(name, value));
   }

   /**
    * Add header.
    *
    * @param header the header
    */
   public static void addHeader(Header header)
   {
      if (header == null)
         throw new IllegalArgumentException("Null header");

      Set<Header> set = tlh.get();
      if (set == null)
      {
         set = new HashSet<Header>();
         tlh.set(set);
      }
      set.add(header);
   }

   /**
    * Add headers.
    *
    * @param headers the headers
    */
   public static void addHeaders(Map<String, String> headers)
   {
      if (headers == null)
         throw new IllegalArgumentException("Null headers");

      boolean success = false;
      try
      {
         for (Map.Entry<String, String> entry : headers.entrySet())
            addHeader(entry.getKey(), entry.getValue());
         success = true;
      }
      finally
      {
         if (success == false)
            clear();
      }
   }

   /**
    * Add headers.
    *
    * @param headers the headers
    */
   public static void addHeaders(Set<Header> headers)
   {
      if (headers == null)
         throw new IllegalArgumentException("Null headers");

      boolean success = false;
      try
      {
         for (Header header : headers)
            addHeader(header);
         success = true;
      }
      finally
      {
         if (success == false)
            clear();
      }
   }
}
