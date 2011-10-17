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

package org.jboss.capedwarf.server.api.persistence;

/**
 * Abstract stateless adapter factory.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
final class TupleHolder
{
   private static ThreadLocal<Tuple> tl = new ThreadLocal<Tuple>();

   static Tuple get()
   {
      Tuple tuple = tl.get();

      if (tuple != null)
         tuple.count++;

      return tuple;
   }

   static Tuple create(StatelessAdapter adapter)
   {
      Tuple tuple = new Tuple();
      tuple.adapter = adapter;
      tuple.count = 1;
      tl.set(tuple);
      return tuple;
   }

   static void close()
   {
      Tuple tuple = tl.get();
      if (tuple == null)
         throw new IllegalStateException("No tuple!");

      tuple.count--;
      if (tuple.count == 0)
      {
         tl.remove();
         tuple.adapter.close();
      }
   }

   static class Tuple
   {
      private StatelessAdapter adapter;
      private int count;

      StatelessAdapter getAdapter()
      {
         return adapter;
      }
   }
}
