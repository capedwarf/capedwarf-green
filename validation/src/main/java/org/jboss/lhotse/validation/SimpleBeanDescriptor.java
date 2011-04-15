package org.jboss.lhotse.validation;

import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * javax.validation bean descriptor.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SimpleBeanDescriptor implements BeanDescriptor
{
   private SimpleValidatorFactory factory;
   private Class<?> beanClass;
   private Map<String, SimplePropertyDescriptor> properties;

   SimpleBeanDescriptor(SimpleValidatorFactory factory, Class<?> beanClass)
   {
      if (beanClass == null)
         throw new IllegalArgumentException("Null bean class");

      this.factory = factory;
      this.beanClass = beanClass;
      this.properties = new HashMap<String, SimplePropertyDescriptor>();
      init();
   }

   /**
    * Initialize properties.
    */
   protected void init()
   {
      Method[] ms = beanClass.getMethods();
      for (Method m : ms)
      {
         String methodName = m.getName();
         if (isPropertyGetter(m))
         {
            Annotation[] da = m.getDeclaredAnnotations();
            for (Annotation a : da)
            {
               Class<? extends Annotation> ac = a.annotationType();
               if (factory.get(ac) != null)
               {
                  SimplePropertyDescriptor pd = properties.get(methodName);
                  if (pd == null)
                  {
                     pd = new SimplePropertyDescriptor(methodName, m.getReturnType());
                     properties.put(methodName, pd);
                  }
                  pd.addConstraintAnnotation(ac);
               }
            }
         }
      }
   }

   protected boolean isPropertyGetter(Method m)
   {
      String name = m.getName();
      if (name.startsWith("get") || name.startsWith("is"))
      {
         Class<?>[] paramTypes = m.getParameterTypes();
         return (paramTypes.length == 0);
      }
      return false;
   }

   public boolean isBeanConstrained()
   {
      return hasConstraints();
   }

   public SimplePropertyDescriptor getConstraintsForProperty(String propertyName)
   {
      return properties.get(propertyName);
   }

   public Set<PropertyDescriptor> getConstrainedProperties()
   {
      return new HashSet<PropertyDescriptor>(properties.values());
   }

   Set<SimplePropertyDescriptor> getConstrainedSimpleProperties()
   {
      return new HashSet<SimplePropertyDescriptor>(properties.values());
   }

   public boolean hasConstraints()
   {
      return properties.isEmpty() == false;
   }

   public Class<?> getElementClass()
   {
      return beanClass;
   }

   public Set<ConstraintDescriptor<?>> getConstraintDescriptors()
   {
      return Collections.emptySet();
   }

   public ConstraintFinder findConstraints()
   {
      return null;
   }
}
