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

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAResource;

/**
 * UserTransaction based Transaction.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class UTTransaction extends UserTransactionAdapter implements Transaction
{
   static Transaction INSTANCE = new UTTransaction();

   public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException
   {
      getUserTransaction().commit();
      cleanup();
   }

   public void rollback() throws IllegalStateException, SystemException
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

   public boolean enlistResource(XAResource xaRes) throws RollbackException, IllegalStateException, SystemException
   {
      return false;
   }

   public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException
   {
      return false;
   }

   public void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException, SystemException
   {
   }
}
