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

package org.jboss.capedwarf.jpa2;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.jboss.capedwarf.jpa.EntityManagerProvider;

/**
 * JPA2 typed query
 *
 * @author Matej Lazar
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings({"unchecked"})
public class TypedProxyingQuery<X> extends NewProxyingQuery implements TypedQuery<X>
{
   private TypedQuery<X> delegate;

   protected TypedProxyingQuery(EntityManagerProvider provider, TypedQuery<X> delegate)
   {
      super(provider, delegate);
      this.delegate = delegate;
   }

   public <T> TypedQuery<X> setParameter(Parameter<T> param, T value)
   {
      return delegate.setParameter(param, value);
   }

   public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType)
   {
      return delegate.setParameter(param, value, temporalType);
   }

   public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType)
   {
      return delegate.setParameter(param, value, temporalType);
   }

   public TypedQuery<X> setLockMode(LockModeType lockMode)
   {
      return delegate.setLockMode(lockMode);
   }

   public int getMaxResults()
   {
      return delegate.getMaxResults();
   }

   public int getFirstResult()
   {
      return delegate.getFirstResult();
   }

   public Map<String, Object> getHints()
   {
      return delegate.getHints();
   }

   @Override
   public X getSingleResult()
   {
      return (X) super.getSingleResult();
   }

   @Override
   public TypedQuery<X> setParameter(String s, Object o)
   {
      return (TypedQuery<X>) super.setParameter(s, o);
   }

   @Override
   public TypedQuery<X> setParameter(String s, Date date, TemporalType temporalType)
   {
      return (TypedQuery<X>) super.setParameter(s, date, temporalType);
   }

   @Override
   public TypedQuery<X> setParameter(String s, Calendar calendar, TemporalType temporalType)
   {
      return (TypedQuery<X>) super.setParameter(s, calendar, temporalType);
   }

   @Override
   public TypedQuery<X> setParameter(int i, Object o)
   {
      return (TypedQuery<X>) super.setParameter(i, o);
   }

   @Override
   public TypedQuery<X> setParameter(int i, Date date, TemporalType temporalType)
   {
      return (TypedQuery<X>) super.setParameter(i, date, temporalType);
   }

   @Override
   public TypedQuery<X> setParameter(int i, Calendar calendar, TemporalType temporalType)
   {
      return (TypedQuery<X>) super.setParameter(i, calendar, temporalType);
   }

   @Override
   public TypedQuery<X> setMaxResults(int i)
   {
      return (TypedQuery<X>) super.setMaxResults(i);
   }

   @Override
   public TypedQuery<X> setFirstResult(int i)
   {
      return (TypedQuery<X>) super.setFirstResult(i);
   }

   @Override
   public TypedQuery<X> setFlushMode(FlushModeType flushModeType)
   {
      return (TypedQuery<X>) super.setFlushMode(flushModeType);
   }

   @Override
   public TypedQuery<X> setHint(String s, Object o)
   {
      return (TypedQuery<X>) super.setHint(s, o);
   }
}
