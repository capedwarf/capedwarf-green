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
import java.util.Set;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.jboss.capedwarf.jpa.EntityManagerProvider;
import org.jboss.capedwarf.jpa.ProxyingQuery;

/**
 * JPA2 typed query
 *
 * @author Matej Lazar
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings({"unchecked"})
public class NewProxyingQuery extends ProxyingQuery implements Query
{
   private Query delegate;

   protected NewProxyingQuery(EntityManagerProvider provider, Query delegate)
   {
      super(provider, delegate);
      this.delegate = delegate;
   }

   public Query setLockMode(LockModeType lockMode)
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

   public Set<Parameter<?>> getParameters()
   {
      return delegate.getParameters();
   }

   public Parameter<?> getParameter(String name)
   {
      return delegate.getParameter(name);
   }

   public <T> Query setParameter(Parameter<T> param, T value)
   {
      return delegate.setParameter(param, value);
   }

   public Query setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType)
   {
      return delegate.setParameter(param, value, temporalType);
   }

   public Query setParameter(Parameter<Date> param, Date value, TemporalType temporalType)
   {
      return delegate.setParameter(param, value, temporalType);
   }

   public <T> Parameter<T> getParameter(String name, Class<T> type)
   {
      return delegate.getParameter(name, type);
   }

   public Parameter<?> getParameter(int position)
   {
      return delegate.getParameter(position);
   }

   public <T> Parameter<T> getParameter(int position, Class<T> type)
   {
      return delegate.getParameter(position, type);
   }

   public boolean isBound(Parameter<?> param)
   {
      return delegate.isBound(param);
   }

   public <T> T getParameterValue(Parameter<T> param)
   {
      return delegate.getParameterValue(param);
   }

   public Object getParameterValue(String name)
   {
      return delegate.getParameter(name);
   }

   public Object getParameterValue(int position)
   {
      return delegate.getParameter(position);
   }

   public FlushModeType getFlushMode()
   {
      return delegate.getFlushMode();
   }

   public LockModeType getLockMode()
   {
      return delegate.getLockMode();
   }

   public <T> T unwrap(Class<T> cls)
   {
      return delegate.unwrap(cls);
   }
}
