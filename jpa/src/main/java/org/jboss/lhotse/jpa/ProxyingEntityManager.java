package org.jboss.lhotse.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

/**
 * Proxy wrapping EntityManager.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
abstract class ProxyingEntityManager extends ProxyingHelper implements EntityManager
{
   private EntityManager delegate;

   ProxyingEntityManager(EntityManager delegate)
   {
      if (delegate == null)
         throw new IllegalArgumentException("Null delegate");

      this.delegate = delegate;
   }

   public void persist(Object o)
   {
      Object entity = getEntity(o);
      delegate.persist(entity);
   }

   public <T> T merge(T t)
   {
      T entity = getEntity(t);
      T merged = delegate.merge(entity);
      return wrap(merged);
   }

   public void remove(Object o)
   {
      Object entity = getEntity(o);
      delegate.remove(entity);
   }

   public <T> T find(Class<T> tClass, Object o)
   {
      T result = delegate.find(tClass, o);
      return (result != null) ? wrap(result) : null;
   }

   public <T> T getReference(Class<T> tClass, Object o)
   {
      T result = delegate.getReference(tClass, o);
      return (result != null) ? wrap(result) : null;
   }

   public void flush()
   {
      delegate.flush();
   }

   public void setFlushMode(FlushModeType flushModeType)
   {
      delegate.setFlushMode(flushModeType);
   }

   public FlushModeType getFlushMode()
   {
      return delegate.getFlushMode();
   }

   public void lock(Object o, LockModeType lockModeType)
   {
      Object entity = getEntity(o);
      delegate.lock(entity, lockModeType);
   }

   public void refresh(Object o)
   {
      Object entity = getEntity(o);
      delegate.refresh(entity);
   }

   public void clear()
   {
      delegate.clear();
   }

   public boolean contains(Object o)
   {
      Object entity = getEntity(o);
      return delegate.contains(entity);
   }

   public Query createQuery(String s)
   {
      Query query = delegate.createQuery(s);
      return new ProxyingQuery(getProvider(), query);
   }

   public Query createNamedQuery(String s)
   {
      Query query = delegate.createNamedQuery(s);
      return new ProxyingQuery(getProvider(), query);
   }

   public Query createNativeQuery(String s)
   {
      Query query = delegate.createNativeQuery(s);
      return new ProxyingQuery(getProvider(), query);
   }

   public Query createNativeQuery(String s, Class aClass)
   {
      Query query = delegate.createNativeQuery(s, aClass);
      return new ProxyingQuery(getProvider(), query);
   }

   public Query createNativeQuery(String s, String s1)
   {
      Query query = delegate.createNativeQuery(s, s1);
      return new ProxyingQuery(getProvider(), query);
   }

   public void close()
   {
      delegate.close();
   }

   public boolean isOpen()
   {
      return delegate.isOpen();
   }

   public EntityTransaction getTransaction()
   {
      return delegate.getTransaction();
   }

   public void joinTransaction()
   {
      delegate.joinTransaction();
   }

   public Object getDelegate()
   {
      return delegate.getDelegate();
   }
}
