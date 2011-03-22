package org.jboss.test.lhotse.jpa.support;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
public class MockQuery implements Query
{
   private Map<String, Object> strings = new HashMap<String, Object>();
   private Map<Integer, Object> indexes = new HashMap<Integer, Object>();

   private String query;

   public MockQuery(String query)
   {
      this.query = query;
   }

   private String getTable()
   {
      String sub = query.substring("select e from ".length());
      int e = sub.indexOf(" ");
      return sub.substring(0, e);
   }

   @SuppressWarnings({"unchecked"})
   public List getResultList()
   {
      Map<Class<?>, Map<Object, Object>> db = MockEM.db;
      try
      {
         String table = getClass().getPackage().getName() + "." + getTable();
         Class<?> tClass = getClass().getClassLoader().loadClass(table);
         Map<Object, Object> map = db.get(tClass);
         return new ArrayList(map.values());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public Object getSingleResult()
   {
      return null;
   }

   public int executeUpdate()
   {
      return 0;
   }

   public Query setMaxResults(int i)
   {
      return null;
   }

   public Query setFirstResult(int i)
   {
      return null;
   }

   public Query setFlushMode(FlushModeType flushModeType)
   {
      return null;
   }

   public Query setHint(String s, Object o)
   {
      return null;
   }

   public Query setParameter(String s, Object o)
   {
      strings.put(s, o);
      return this;
   }

   public Query setParameter(String s, Date date, TemporalType temporalType)
   {
      strings.put(s, date);
      return this;
   }

   public Query setParameter(String s, Calendar calendar, TemporalType temporalType)
   {
      strings.put(s, calendar);
      return this;
   }

   public Query setParameter(int i, Object o)
   {
      indexes.put(i, o);
      return this;
   }

   public Query setParameter(int i, Date date, TemporalType temporalType)
   {
      indexes.put(i, date);
      return this;
   }

   public Query setParameter(int i, Calendar calendar, TemporalType temporalType)
   {
      indexes.put(i, calendar);
      return this;
   }
}
