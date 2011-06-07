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

package org.jboss.capedwarf.server.jee.tx;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.jboss.capedwarf.server.api.tx.TransactionPropagationType;
import org.jboss.capedwarf.server.api.tx.Transactional;
import org.jboss.capedwarf.server.api.tx.TxInterceptorDelegate;
import org.jboss.logging.Logger;

/**
 * Transaction interceptor.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 * @author Matej Lazar added transaction types based on org.jboss.ejb3.tx2.impl.CMTTxInterceptor
 */
@Alternative
public class TransactionInterceptor implements TxInterceptorDelegate, Serializable
{
   private static final long serialVersionUID = 1L;
   private static final Logger log = Logger.getLogger(TransactionInterceptor.class);

   private TransactionManager tm;

   private transient Map<AnnotatedElement, TransactionMetadata> transactionMetadata = new HashMap<AnnotatedElement, TransactionMetadata>();

   /**
    * The <code>endTransaction</code> method ends a transaction and
    * translates any exceptions into
    * TransactionRolledBack[Local]Exception or SystemException.
    *
    * @param tm a <code>TransactionManager</code> value
    * @param tx a <code>Transaction</code> value
    */
   protected void endTransaction(TransactionManager tm, Transaction tx)
   {
      try
      {
         if (tx != tm.getTransaction())
         {
            throw new IllegalStateException("Wrong tx on thread: expected " + tx + ", actual " + tm.getTransaction());
         }

         if (tx.getStatus() == Status.STATUS_MARKED_ROLLBACK)
         {
            tm.rollback();
         }
         else
         {
            // Commit tx
            // This will happen if
            // a) everything goes well
            // b) app. exception was thrown
            tm.commit();
         }
      }
      catch (RollbackException e)
      {
         handleEndTransactionException(e);
      }
      catch (HeuristicMixedException e)
      {
         handleEndTransactionException(e);
      }
      catch (HeuristicRollbackException e)
      {
         handleEndTransactionException(e);
      }
      catch (SystemException e)
      {
         handleEndTransactionException(e);
      }
   }

   public Object invoke(final InvocationContext invocation) throws Exception
   {
      TransactionPropagationType propType = getPropType(invocation);
      switch (propType)
      {
         case MANDATORY:
            return mandatory(invocation);
         case NEVER:
            return never(invocation);
         case NOT_SUPPORTED:
            return notSupported(invocation);
         case REQUIRED:
            return required(invocation);
         case REQUIRES_NEW:
            return requiresNew(invocation);
         case SUPPORTS:
            return supports(invocation);
         default:
            throw new IllegalStateException("Unexpected tx propagation type " + propType + " on " + invocation);
      }
   }

   private TransactionPropagationType getPropType(final InvocationContext invocation)
   {
      TransactionMetadata transMeta = lookupTransactionMetadata(invocation.getMethod());
      /** is method annotated */
      if (transMeta.isAnnotationPresent())
      {
         return transMeta.getPropType();
      }
      else
      {
         transMeta = lookupTransactionMetadata(invocation.getTarget().getClass());
         /** if method is not annotated, then class must be */
         if (transMeta.isAnnotationPresent())
         {
            return transMeta.getPropType();
         }
         else
         {
            throw new RuntimeException("No Transacional annotation found. " + invocation);
         }
      }
   }

   protected Object invokeInCallerTx(InvocationContext invocation, Transaction tx) throws Exception
   {
      try
      {
         return invocation.proceed();
      }
      catch (Throwable t)
      {
         handleExceptionInCallerTx(invocation, t, tx);
      }
      throw new RuntimeException("UNREACHABLE");
   }

   protected Object invokeInNoTx(InvocationContext invocation) throws Exception
   {
      return invocation.proceed();
   }

   protected Object invokeInOurTx(InvocationContext invocation, TransactionManager tm) throws Exception
   {
      tm.begin();
      Transaction tx = tm.getTransaction();
      try
      {
         return invocation.proceed();
      }
      catch (Throwable t)
      {
         handleExceptionInOurTx(invocation, t, tx);
      }
      finally
      {
         endTransaction(tm, tx);
      }
      throw new RuntimeException("UNREACHABLE");
   }

   protected Object mandatory(InvocationContext invocation) throws Exception
   {
      Transaction tx = tm.getTransaction();
      if (tx == null)
      {
         //TODO throw typed exception
         // throw new EJBTransactionRequiredException("Transaction is required for invocation: " + invocation);
         throw new RuntimeException("Transaction is required for invocation: " + invocation);
      }
      return invokeInCallerTx(invocation, tx);
   }

   protected Object never(InvocationContext invocation) throws Exception
   {
      if (tm.getTransaction() != null)
      {
         //TODO throw typed exception
         //throw new EJBException("Transaction present on server in Never call (EJB3 13.6.2.6)");
         throw new RuntimeException("Transaction present on server in Never call (EJB3 13.6.2.6)");
      }
      return invokeInNoTx(invocation);
   }

   protected Object notSupported(InvocationContext invocation) throws Exception
   {
      Transaction tx = tm.getTransaction();
      if (tx != null)
      {
         tm.suspend();
         try
         {
            return invokeInNoTx(invocation);
         }
         finally
         {
            tm.resume(tx);
         }
      }
      else
      {
         return invokeInNoTx(invocation);
      }
   }

   protected Object required(InvocationContext invocation) throws Exception
   {
      Transaction tx = tm.getTransaction();
      if (tx == null)
      {
         return invokeInOurTx(invocation, tm);
      }
      else
      {
         return invokeInCallerTx(invocation, tx);
      }
   }

   protected Object requiresNew(InvocationContext invocation) throws Exception
   {
      Transaction tx = tm.getTransaction();
      if (tx != null)
      {
         tm.suspend();
         try
         {
            return invokeInOurTx(invocation, tm);
         }
         finally
         {
            tm.resume(tx);
         }
      }
      else
      {
         return invokeInOurTx(invocation, tm);
      }
   }


   protected Object supports(InvocationContext invocation) throws Exception
   {
      Transaction tx = tm.getTransaction();
      if (tx == null)
      {
         return invokeInNoTx(invocation);
      }
      else
      {
         return invokeInCallerTx(invocation, tx);
      }
   }

   /**
    * The <code>setRollbackOnly</code> method calls setRollbackOnly()
    * on the invocation's transaction and logs any exceptions than may
    * occur.
    *
    * @param tx the transaction
    */
   protected void setRollbackOnly(Transaction tx)
   {
      try
      {
         tx.setRollbackOnly();
      }
      catch (SystemException ex)
      {
         log.error("SystemException while setting transaction for rollback only", ex);
      }
      catch (IllegalStateException ex)
      {
         log.error("IllegalStateException while setting transaction for rollback only", ex);
      }
   }

   protected void handleEndTransactionException(Exception e)
   {
      //TODO throw typed exception
      throw new RuntimeException("Transaction rolled back", e);
   }

   protected void handleExceptionInCallerTx(InvocationContext invocation, Throwable t, Transaction tx) throws Exception
   {
      setRollbackOnly(tx);
      log.error(t);
      throw (Exception) t;
   }

   public void handleExceptionInOurTx(InvocationContext invocation, Throwable t, Transaction tx) throws Exception
   {
      setRollbackOnly(tx);
      throw (Exception) t;
   }

   private TransactionMetadata lookupTransactionMetadata(AnnotatedElement element)
   {
      TransactionMetadata metadata = transactionMetadata.get(element);
      if (metadata == null)
         metadata = loadMetadata(element);

      return metadata;
   }

   private synchronized TransactionMetadata loadMetadata(AnnotatedElement element)
   {
      if (transactionMetadata.containsKey(element) == false)
      {
         TransactionMetadata metadata = new TransactionMetadata(element);
         transactionMetadata.put(element, metadata);
         return metadata;
      }

      return transactionMetadata.get(element);
   }

   private class TransactionMetadata
   {
      private boolean annotationPresent;
      private TransactionPropagationType propType;

      public TransactionMetadata(AnnotatedElement element)
      {
         annotationPresent = element.isAnnotationPresent(Transactional.class);

         if (annotationPresent)
         {
            propType = element.getAnnotation(Transactional.class).value();
         }
      }

      public boolean isAnnotationPresent()
      {
         return annotationPresent;
      }

      /**
       * @return the propType
       */
      public TransactionPropagationType getPropType()
      {
         return propType;
      }
   }

   @Inject
   public void setTransactionManager(TransactionManager transactionManager)
   {
      this.tm = transactionManager;
   }
}
