package org.jboss.lhotse.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handle relationships.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class Relationships
{
   /** The get entity id mapping */
   private static Map<Class<?>, Map<Class<?>, Method>> getIdMapping = new ConcurrentHashMap<Class<?>, Map<Class<?>, Method>>();

   /** The set entity id mapping */
   private static Map<Class<?>, Map<Class<?>, Method>> setIdMapping = new ConcurrentHashMap<Class<?>, Map<Class<?>, Method>>();

   /** The one-to-many queries */
   private static Map<String, String> otmQueries = new ConcurrentHashMap<String, String>();

   static Object handleManyToOne(Object entity, Method method, Object[] args, ManyToOne otm, EntityManagerProvider provider) throws Throwable
   {
      String methodName = method.getName();
      if (methodName.startsWith("get"))
         return getMTO(method, otm, entity, provider);
      else
      {
         if (args == null || args.length != 1 || args[0] == null)
            throw new IllegalArgumentException("Illegals args: " + Arrays.toString(args));

         setMTO(method, otm, entity, args[0]);
         return null;
      }
   }

   @SuppressWarnings({"unchecked"})
   static Collection handleOneToMany(Object entity, Method method, OneToMany otm, ProxyingHelper helper) throws Throwable
   {
      if (entity instanceof Entity == false)
         throw new IllegalArgumentException("Entity is not an entity: " + entity);

      Class<?> ownerClass = entity.getClass();
      String key = ownerClass.getName() + "::" + method.getName();
      String queryString = otmQueries.get(key);
      if (queryString == null)
      {
         Class<?> entityClass = otm.element();
         if (entityClass == Void.class)
         {
            Type rt = method.getGenericReturnType();
            if (rt instanceof ParameterizedType == false)
               throw new IllegalArgumentException("Cannot get exact type: " + rt);

            ParameterizedType pt = (ParameterizedType) rt;
            Type[] ats = pt.getActualTypeArguments();
            if (ats == null || ats.length != 1 || (ats[0] instanceof Class == false))
               throw new IllegalArgumentException("Illegal actual type: " + Arrays.toString(ats));

            entityClass = (Class) ats[0];
         }

         String joinName = otm.join();
         if (joinName == null || joinName.length() == 0)
         {
            String ownerName = ownerClass.getSimpleName().toLowerCase();
            joinName = ownerName + "Id";
         }

         StringBuilder builder = new StringBuilder("select e from ");
         builder.append(entityClass.getSimpleName());
         builder.append(" e where e.").append(joinName).append(" = ?1 ");
         builder.append(otm.orderBy());

         queryString = builder.toString().trim();
         otmQueries.put(key, queryString);
      }

      Object id = ((Entity)entity).getId();
      EntityManagerProvider provider = helper.getProvider();
      EntityManager em = provider.getEntityManager();
      try
      {
         Query query = em.createQuery(queryString);
         query.setParameter(1, id);
         List result = query.getResultList();
         Collection collection = otm.type().newInstance();
         if (ProxyingUtils.isDisabled())
         {
            collection.addAll(result);   
         }
         else
         {
            for (Object obj : result)
               collection.add(helper.wrap(obj));
         }
         return collection;
      }
      finally
      {
         provider.close(em);
      }
   }

   private static Object getMTO(Method method, ManyToOne mto, Object entity, EntityManagerProvider provider) throws Throwable
   {
      Object rel = method.invoke(entity);
      if (rel == null)
      {
         Class<?> ec = entity.getClass();
         String methodName = method.getName();
         Class<?> entityClass = method.getReturnType();
         Map<Class<?>, Method> map = getIdMapping.get(ec);
         if (map == null)
         {
            map = new ConcurrentHashMap<Class<?>, Method>();
            getIdMapping.put(ec, map);
         }
         Method idMethod = map.get(entityClass);
         if (idMethod == null)
         {
            String idName = mto.id();
            if (idName == null || idName.length() == 0)
            {
               idName = methodName + "Id";
            }
            idMethod = ec.getMethod(idName);
            map.put(entityClass, idMethod);
         }
         Object id = idMethod.invoke(entity); // get rel id

         EntityManager em = provider.getEntityManager();
         try
         {
            rel = em.find(entityClass, id);
         }
         finally
         {
            provider.close(em);
         }

         Method setter = ec.getMethod("s" + methodName.substring(1), entityClass);
         setter.invoke(entity, rel); // set rel
      }
      return rel;
   }

   private static void setMTO(Method method, ManyToOne mto, Object entity, Object value) throws Throwable
   {
      if (value instanceof Entity == false)
         throw new IllegalArgumentException("Value is not an entity: " + value);

      Class<?> ec = entity.getClass();
      String methodName = method.getName();
      Class<?> entityClass = method.getParameterTypes()[0];
      Map<Class<?>, Method> map = setIdMapping.get(ec);
      if (map == null)
      {
         map = new ConcurrentHashMap<Class<?>, Method>();
         setIdMapping.put(ec, map);
      }
      Method idMethod = map.get(entityClass);
      if (idMethod == null)
      {
         String idName = mto.id();
         if (idName == null || idName.length() == 0)
         {
            idName = methodName + "Id";
         }
         idMethod = ec.getMethod(idName, Long.class);
         map.put(entityClass, idMethod);
      }
      Object id = ((Entity)value).getId();
      idMethod.invoke(entity, id); // set rel id
   }
}
