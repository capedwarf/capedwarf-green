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

package org.jboss.lhotse.sqlite;

import java.util.HashMap;
import java.util.Map;

/**
 * SQLite type / affinity.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public enum SQLiteTypes
{
   NONE,
   INTEGER,
   TEXT,
   REAL,
   NUMERIC,
   BLOB;

   private static final Map<Class<?>, SQLiteTypes> affinities = new HashMap<Class<?>, SQLiteTypes>();

   static
   {
      affinities.put(String.class, TEXT);
      affinities.put(Byte.class, INTEGER);
      affinities.put(byte.class, INTEGER);
      affinities.put(Short.class, INTEGER);
      affinities.put(short.class, INTEGER);
      affinities.put(Integer.class, INTEGER);
      affinities.put(int.class, INTEGER);
      affinities.put(Long.class, INTEGER);
      affinities.put(long.class, INTEGER);
      affinities.put(Float.class, REAL);
      affinities.put(float.class, REAL);
      affinities.put(Double.class, REAL);
      affinities.put(double.class, REAL);
      affinities.put(Boolean.class, INTEGER);
      affinities.put(boolean.class, INTEGER);
      affinities.put(byte[].class, BLOB);
   }

   /**
    * Get sqlite type mapping.
    *
    * @param type the java type
    * @return sql type
    */
   public static SQLiteTypes getSQLType(Class<?> type)
   {
      if (type == null)
         return NONE;

      if (Enum.class.isAssignableFrom(type))
         return INTEGER;

      SQLiteTypes affinity = affinities.get(type);
      return (affinity != null) ? affinity : NONE;
   }
}
