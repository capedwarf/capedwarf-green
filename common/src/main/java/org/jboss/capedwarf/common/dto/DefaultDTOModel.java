package org.jboss.capedwarf.common.dto;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Default DTO model.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DefaultDTOModel<U extends Serializable> implements DTOModel<U>
{
   private Class<U> entityClass;
   private boolean needsClassName;
   private Method idMethod;

   private Class<?> dto;
   private Map<Method, Method> properties;
   private Map<String, ValueConverter> converters;

   public DefaultDTOModel(Class<U> entityClass)
   {
      if (entityClass == null)
         throw new IllegalArgumentException("Null entity class");

      this.entityClass = entityClass;
      init();
   }

   private void init()
   {
      DTOClass dtoc = entityClass.getAnnotation(DTOClass.class);
      if (dtoc == null)
         throw new IllegalArgumentException("No such DTOClass: " + entityClass);
      this.dto = dtoc.value();

      needsClassName = dtoc.needsClassName();
      try
      {
         idMethod = entityClass.getMethod("getId");
         if (idMethod.getReturnType() != Long.class)
            idMethod = null; // ignore non Long id
      }
      catch (Throwable ignored)
      {
      }

      try
      {
         properties = new HashMap<Method, Method>();
         converters = new HashMap<String, ValueConverter>();

         resolve(entityClass);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private void resolve(Class<?> clazz) throws Exception
   {
      Method[] methods = clazz.getMethods();
      for (Method m : methods)
      {
         DTOProperty dtop = m.getAnnotation(DTOProperty.class);
         if (dtop != null)
         {
            String name = m.getName();
            if (name.startsWith("get") == false && name.startsWith("is") == false)
               throw new IllegalArgumentException("Not a getter method: " + m);

            if (converters.containsKey(name) == false)
            {
               String dp = dtop.property();
               String pn;
               if (dp != null && dp.length() > 0)
               {
                  pn = dp;
               }
               else
               {
                  if (name.startsWith("is"))
                     pn = "set" + name.substring(2); // is
                  else
                     pn = "s" + name.substring(1); //get
               }
               Method dtoM = dto.getMethod(pn);
               properties.put(m, dtoM);

               ValueConverter vc = getVC(dtop);
               converters.put(name, vc);
            }
         }
      }
   }

   private ValueConverter getVC(DTOProperty dtop) throws Exception
   {
      Class<? extends ValueConverter> vcc = dtop.converter();
      if (vcc == NoopValueConverter.class)
         return NoopValueConverter.INSTANCE;
      else if (vcc == StringValueConverter.class)
         return StringValueConverter.INSTANCE;

      return vcc.newInstance();
   }

   @SuppressWarnings({"unchecked"})
   public Object toDTO(U entity)
   {
      try
      {
         Object dtoInstance = dto.newInstance();

         if (dtoInstance instanceof Identity)
         {
            Identity identity = (Identity) dtoInstance;
            if (needsClassName)
               identity.setClassName(entityClass.getName());
            if (idMethod != null)
               identity.setId((Long) idMethod.invoke(entity));
         }

         for (Method key : properties.keySet())
         {
            Method dtoProperty = properties.get(key);
            ValueConverter vc = converters.get(key.getName());
            Object value = vc.convert(key.invoke(entity));
            dtoProperty.invoke(dtoInstance, value);
         }
         return dtoInstance;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
