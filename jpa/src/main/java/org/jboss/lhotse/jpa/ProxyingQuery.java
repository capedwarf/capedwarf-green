package org.jboss.lhotse.jpa;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Proxy wrapping Query.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class ProxyingQuery extends ProxyingHelper implements Query
{
   private EntityManagerProvider provider;
   private Query delegate;

   ProxyingQuery(EntityManagerProvider provider, Query delegate)
   {
      if (provider == null)
         throw new IllegalArgumentException("Null provider");
      if (delegate == null)
         throw new IllegalArgumentException("Null delegate");

      this.provider = provider;
      this.delegate = delegate;
   }

   EntityManagerProvider getProvider()
   {
      return provider;
   }

   @SuppressWarnings({"unchecked"})
   public List getResultList()
   {
      List result = delegate.getResultList();
      List list = new ArrayList();
      for (Object entity : result)
         list.add(wrap(entity));

      return list;
   }

   public Object getSingleResult()
   {
      Object entity = delegate.getSingleResult();
      return wrap(entity);
   }

   public int executeUpdate()
   {
      return delegate.executeUpdate();
   }

   public Query setMaxResults(int i)
   {
      return delegate.setMaxResults(i);
   }

   public Query setFirstResult(int i)
   {
      return delegate.setFirstResult(i);
   }

   public Query setFlushMode(FlushModeType flushModeType)
   {
      return delegate.setFlushMode(flushModeType);
   }

   public Query setHint(String s, Object o)
   {
      return delegate.setHint(s, o);
   }

   public Query setParameter(String s, Object o)
   {
      return delegate.setParameter(s, o);
   }

   public Query setParameter(String s, Date date, TemporalType temporalType)
   {
      return delegate.setParameter(s, date, temporalType);
   }

   public Query setParameter(String s, Calendar calendar, TemporalType temporalType)
   {
      return delegate.setParameter(s, calendar, temporalType);
   }

   public Query setParameter(int i, Object o)
   {
      return delegate.setParameter(i, o);
   }

   public Query setParameter(int i, Date date, TemporalType temporalType)
   {
      return delegate.setParameter(i, date, temporalType);
   }

   public Query setParameter(int i, Calendar calendar, TemporalType temporalType)
   {
      return delegate.setParameter(i, calendar, temporalType);
   }
}
