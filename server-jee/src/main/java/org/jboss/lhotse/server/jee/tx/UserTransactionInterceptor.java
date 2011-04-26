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

package org.jboss.lhotse.server.jee.tx;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.lhotse.server.api.tx.TransactionPropagationType;
import org.jboss.lhotse.server.api.tx.Transactional;
import org.jboss.lhotse.server.api.tx.TxInterceptorDelegate;
import org.jboss.logging.Logger;

/**
 * UserTransaction interceptor.
 * Note: no nested tx support!
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class UserTransactionInterceptor implements TxInterceptorDelegate, Serializable
{
   private static final long serialVersionUID = 1L;
   private static final Logger log = Logger.getLogger(UserTransactionInterceptor.class);

   private transient UserTransaction tx;

   private transient Map<AnnotatedElement, TransactionMetadata> transactionMetadata = new HashMap<AnnotatedElement, TransactionMetadata>();

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

   /**
    * The <code>endTransaction</code> method ends a transaction and
    * translates any exceptions into
    * TransactionRolledBack[Local]Exception or SystemException.
    */
   protected void endTransaction()
   {
      try
      {
         if (tx.getStatus() == Status.STATUS_MARKED_ROLLBACK)
         {
            tx.rollback();
         }
         else
         {
            // Commit tx
            // This will happen if
            // a) everything goes well
            // b) app. exception was thrown
            tx.commit();
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

   protected Object invokeInCallerTx(InvocationContext invocation) throws Exception
   {
      try
      {
         return invocation.proceed();
      }
      catch (Throwable t)
      {
         handleExceptionInCallerTx(invocation, t);
      }
      throw new RuntimeException("UNREACHABLE");
   }

   protected Object invokeInNoTx(InvocationContext invocation) throws Exception
   {
      return invocation.proceed();
   }

   protected Object invokeInOurTx(InvocationContext invocation) throws Exception
   {
      tx.begin();
      try
      {
         return invocation.proceed();
      }
      catch (Throwable t)
      {
         handleExceptionInOurTx(invocation, t);
      }
      finally
      {
         endTransaction();
      }
      throw new RuntimeException("UNREACHABLE");
   }

   protected Object mandatory(InvocationContext invocation) throws Exception
   {
      if (tx.getStatus() != Status.STATUS_ACTIVE)
      {
         //TODO throw typed exception
         // throw new EJBTransactionRequiredException("Transaction is required for invocation: " + invocation);
         throw new RuntimeException("Transaction is required for invocation: " + invocation);
      }
      return invokeInCallerTx(invocation);
   }

   protected Object never(InvocationContext invocation) throws Exception
   {
      if (tx.getStatus() != Status.STATUS_NO_TRANSACTION)
      {
         //TODO throw typed exception
         throw new RuntimeException("Transaction present on server in Never call (EJB3 13.6.2.6)");
      }
      return invokeInNoTx(invocation);
   }

   protected Object notSupported(InvocationContext invocation) throws Exception
   {
      // TODO
      return invokeInNoTx(invocation);
   }

   protected Object required(InvocationContext invocation) throws Exception
   {
      if (tx.getStatus() == Status.STATUS_NO_TRANSACTION)
      {
         return invokeInOurTx(invocation);
      }
      else
      {
         return invokeInCallerTx(invocation);
      }
   }

   protected Object requiresNew(InvocationContext invocation) throws Exception
   {
      if (tx.getStatus() == Status.STATUS_ACTIVE)
      {
         throw new NotSupportedException("Requires-New is not supported.");
      }
      else
      {
         return invokeInOurTx(invocation);
      }
   }


   protected Object supports(InvocationContext invocation) throws Exception
   {
      if (tx.getStatus() == Status.STATUS_NO_TRANSACTION)
      {
         return invokeInNoTx(invocation);
      }
      else
      {
         return invokeInCallerTx(invocation);
      }
   }

   /**
    * The <code>setRollbackOnly</code> method calls setRollbackOnly()
    * on the invocation's transaction and logs any exceptions than may
    * occur.
    */
   protected void setRollbackOnly()
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

   protected void handleExceptionInCallerTx(InvocationContext invocation, Throwable t) throws Exception
   {
      setRollbackOnly();
      log.error(t);
      throw (Exception) t;
   }

   public void handleExceptionInOurTx(InvocationContext invocation, Throwable t) throws Exception
   {
      setRollbackOnly();
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
   public void setUserTransaction(UserTransaction tx)
   {
      this.tx = tx;
   }
}
