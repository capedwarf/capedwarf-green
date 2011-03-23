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

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OptimisticLockException;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Transaction interceptor.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Transactional
@Interceptor
public class TransactionInterceptor implements Serializable
{
   private static final long serialVersionUID = 1L;

   private transient static ThreadLocal<Tuple> emTL = new ThreadLocal<Tuple>();
   private transient Map<AnnotatedElement, TransactionMetadata> transactionMetadata = new HashMap<AnnotatedElement, TransactionMetadata>();
   private EntityManagerFactory factory;

   /**
    * Get thread bound entity manager.
    *
    * @return entity manager
    */
   public static EntityManager getEntityManager()
   {
      Tuple tuple = emTL.get();
      return tuple != null ? tuple.em : null;
   }

   @AroundInvoke
   public Object aroundInvoke(final InvocationContext invocation) throws Exception
   {
      Tuple tuple = emTL.get();
      if (tuple == null)
      {
         tuple = new Tuple(factory.createEntityManager());
         emTL.set(tuple);
      }
      tuple.count++;

      try
      {
         return new Work()
         {
            @Override
            protected Object work() throws Exception
            {
               return invocation.proceed();
            }

            @Override
            protected boolean isNewTransactionRequired(boolean transactionActive)
            {
               return isNewTransactionRequired(invocation.getMethod(), invocation.getTarget().getClass(), transactionActive);
            }

            private boolean isNewTransactionRequired(Method method, Class beanClass, boolean transactionActive)
            {
               TransactionMetadata metadata = lookupTransactionMetadata(method);
               if (metadata.isNewTransactionRequired(transactionActive))
               {
                  return true;
               }
               else
               {
                  return lookupTransactionMetadata(beanClass).isNewTransactionRequired(transactionActive);
               }
            }

            protected Object handleException(Exception e)
            {
               if (e.getCause() instanceof OptimisticLockException)
               {
                  Object target = invocation.getTarget();
                  if (target instanceof OptimisticLockExceptionHandler)
                  {
                     OptimisticLockExceptionHandler oleh = (OptimisticLockExceptionHandler) target;
                     return oleh.handleVersionConflict(invocation.getParameters());
                  }
               }
               return null;
            }

         }.workInTransaction(tuple.em.getTransaction());
      }
      finally
      {
         tuple.count--;
         if (tuple.count == 0)
         {
            emTL.remove();
            tuple.em.close();
         }
      }
   }

   private TransactionMetadata lookupTransactionMetadata(AnnotatedElement element)
   {
      TransactionMetadata metadata = transactionMetadata.get(element);

      if (metadata == null)
      {
         metadata = loadMetadata(element);
      }

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

      public boolean isNewTransactionRequired(boolean transactionActive)
      {
         return propType != null && propType.isNewTransactionRequired(transactionActive);
      }
   }

   private static class Tuple
   {
      private EntityManager em;
      private int count;

      private Tuple(EntityManager em)
      {
         this.em = em;
      }
   }

   @Inject
   public void setFactory(EntityManagerFactory factory)
   {
      this.factory = factory;
   }
}
