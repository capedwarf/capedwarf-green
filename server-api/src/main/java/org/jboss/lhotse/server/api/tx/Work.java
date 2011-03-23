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

package org.jboss.lhotse.server.api.tx;

import javax.persistence.EntityTransaction;

/**
 * Work in transaction.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class Work<T>
{
   /**
    * Do actual work.
    *
    * @return the result
    * @throws Exception for any error
    */
   protected abstract T work() throws Exception;

   /**
    * Do we need new transaction.
    *
    * @param transactionActive is current transaction active
    * @return true if we need new transaction, false otherwise
    */
   protected boolean isNewTransactionRequired(boolean transactionActive)
   {
      return transactionActive == false;
   }

   public final T workInTransaction(EntityTransaction transaction) throws Exception
   {
      boolean newTransactionRequired = isNewTransactionRequired(transaction.isActive());
      EntityTransaction userTransaction = newTransactionRequired ? transaction : null;

      if (newTransactionRequired)
      {
         userTransaction.begin();
      }

      try
      {
         T result = work();

         if (newTransactionRequired)
         {
            if (transaction.getRollbackOnly())
            {
               userTransaction.rollback();
            }
            else
            {
               userTransaction.commit();
            }
         }

         return result;
      }
      catch (Exception e)
      {
         T fallback = handleException(e);
         if (newTransactionRequired && fallback == null && userTransaction.isActive())
         {
            try
            {
               userTransaction.rollback();
            }
            catch (Exception ignored)
            {
               // not really useful
            }
         }
         if (fallback == null)
            throw e;

         return fallback;
      }

   }

   /**
    * Handle exception.
    *
    * @param e the exception
    * @return null if we should throw the exception, proper fallback result otherwise
    */
   protected T handleException(Exception e)
   {
      return null;
   }
}
