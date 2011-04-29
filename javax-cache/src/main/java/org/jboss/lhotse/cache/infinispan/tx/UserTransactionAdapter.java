/*
* JBoss, Home of Professional Open Source
* Copyright $today.year Red Hat Inc. and/or its affiliates and other
* contributors as indicated by the @author tags. All rights reserved.
* See the copyright.txt in the distribution for a full listing of
* individual contributors.
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

package org.jboss.lhotse.cache.infinispan.tx;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 * UserTransaction based TransactionManager.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class UserTransactionAdapter
{
   private static ThreadLocal<UserTransaction> uts = new ThreadLocal<UserTransaction>();
   public static boolean ignoreThreadLocal;

   static
   {
      ignoreThreadLocal = Boolean.getBoolean("org.jboss.lhotse.cache.infinispan.tx.ignoreThreadLocal");
   }

   protected UserTransaction getUserTransaction() throws SystemException
   {
      if (ignoreThreadLocal == false)
      {
         UserTransaction ut = uts.get();
         if (ut == null)
         {
            ut = getUserTransactionInternal();
            uts.set(ut);
         }
         return ut;
      }
      else
      {
         return getUserTransactionInternal();
      }
   }

   /**
    * Set UserTransaction.
    *
    * @param ut the user transaction
    */
   public static void setup(UserTransaction ut)
   {
      if (ignoreThreadLocal == false)
         uts.set(ut);
   }

   /**
    * Cleanup current user transaction.
    */
   public static void cleanup()
   {
      if (ignoreThreadLocal == false)
         uts.remove();
   }

   protected UserTransaction getUserTransactionInternal() throws SystemException
   {
      try
      {
         Context context = new InitialContext();
         try
         {
            Object lookup = context.lookup("java:comp/UserTransaction");
            return UserTransaction.class.cast(lookup);
         }
         finally
         {
            context.close();
         }
      }
      catch (NamingException e)
      {
         throw new SystemException(e.getMessage());
      }
   }
}
