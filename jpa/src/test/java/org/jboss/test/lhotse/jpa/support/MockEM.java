package org.jboss.test.lhotse.jpa.support;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.lhotse.jpa.Entity;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MockEM implements EntityManager
{
   static Map<Class<?>, Map<Object, Object>> db = new ConcurrentHashMap<Class<?>, Map<Object, Object>>();

   public void persist(Object o)
   {
      Class<?> clazz = o.getClass();
      Map<Object, Object> map = db.get(clazz);
      Entity entity = (Entity) o;
      if (map == null)
      {
         map = new ConcurrentHashMap<Object, Object>();
         db.put(clazz, map);
      }
      map.put(entity.getId(), o);
   }

   public <T> T merge(T t)
   {
      return t;
   }

   public void remove(Object o)
   {
   }

   @SuppressWarnings({"unchecked"})
   public <T> T find(Class<T> tClass, Object o)
   {
      Map<Object, Object> map = db.get(tClass);
      return (map != null) ? (T) map.get(o) : null;
   }

   public <T> T getReference(Class<T> tClass, Object o)
   {
      return null;
   }

   public void flush()
   {
   }

   public void setFlushMode(FlushModeType flushModeType)
   {
   }

   public FlushModeType getFlushMode()
   {
      return null;
   }

   public void lock(Object o, LockModeType lockModeType)
   {
   }

   public void refresh(Object o)
   {
   }

   public void clear()
   {
   }

   public boolean contains(Object o)
   {
      return false;
   }

   public Query createQuery(String s)
   {
      return new MockQuery(s);
   }

   public Query createNamedQuery(String s)
   {
      return new MockQuery(s);
   }

   public Query createNativeQuery(String s)
   {
      return new MockQuery(s);
   }

   public Query createNativeQuery(String s, Class aClass)
   {
      return new MockQuery(s);
   }

   public Query createNativeQuery(String s, String s1)
   {
      return new MockQuery(s);
   }

   public void close()
   {
   }

   public boolean isOpen()
   {
      return false;
   }

   public EntityTransaction getTransaction()
   {
      return null;
   }

   public void joinTransaction()
   {
   }

   public Object getDelegate()
   {
      return null;
   }
}
