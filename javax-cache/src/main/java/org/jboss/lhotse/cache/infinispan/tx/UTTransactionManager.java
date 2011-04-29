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

import java.util.logging.Logger;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * UserTransaction based TransactionManager.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class UTTransactionManager extends UserTransactionAdapter implements TransactionManager
{
   private static Logger log = Logger.getLogger(UTTransactionManager.class.getName());
   static TransactionManager INSTANCE = new UTTransactionManager();

   public void begin() throws NotSupportedException, SystemException
   {
      getUserTransaction().begin();
   }

   public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      getUserTransaction().commit();
      cleanup();
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      getUserTransaction().rollback();
      cleanup();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      getUserTransaction().setRollbackOnly();
   }

   public int getStatus() throws SystemException
   {
      return getUserTransaction().getStatus();
   }

   public Transaction getTransaction() throws SystemException
   {
      return UTTransaction.INSTANCE;
   }

   public void setTransactionTimeout(int seconds) throws SystemException
   {
      getUserTransaction().setTransactionTimeout(seconds);
   }

   public Transaction suspend() throws SystemException
   {
      return UTTransaction.INSTANCE;
   }

   public void resume(Transaction tx) throws InvalidTransactionException, IllegalStateException, SystemException
   {
      log.fine("resume() not supported");
   }
}
