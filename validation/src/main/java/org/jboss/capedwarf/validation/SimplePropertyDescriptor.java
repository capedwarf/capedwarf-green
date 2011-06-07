package org.jboss.capedwarf.validation;

import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.capedwarf.validation.api.MessageTemplateKey;

/**
 * javax.validation property descriptor.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SimplePropertyDescriptor implements PropertyDescriptor
{
   private String propertyName;
   private Class<?> propertyType;
   private Set<Class<? extends Annotation>> constraintAnnotations = new HashSet<Class<? extends Annotation>>();

   SimplePropertyDescriptor(String propertyName, Class<?> propertyType)
   {
      this.propertyName = propertyName;
      this.propertyType = propertyType;
   }

   void addConstraintAnnotation(Class<? extends Annotation> ac)
   {
      constraintAnnotations.add(ac);
   }

   Set<Class<? extends Annotation>> getConstraintAnnotations()
   {
      return constraintAnnotations;
   }

   Annotation getAnnotation(Class<?> clazz, Class<? extends Annotation> ac)
   {
      try
      {
         Method m = clazz.getMethod(propertyName);
         return m.getAnnotation(ac);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   Object getValue(Object object)
   {
      try
      {
         Class<?> clazz = object.getClass();
         Method m = clazz.getMethod(propertyName);
         return m.invoke(object);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   String getMessageTemplateKey(Class<?> beanClass)
   {
      try
      {
         Method m = beanClass.getMethod(propertyName);
         MessageTemplateKey mtk = m.getAnnotation(MessageTemplateKey.class);
         if (mtk != null)
            return mtk.value();

         mtk = beanClass.getAnnotation(MessageTemplateKey.class);
         return mtk != null ? mtk.value() : null;
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public boolean isCascaded()
   {
      return false;
   }

   public String getPropertyName()
   {
      return propertyName;
   }

   public boolean hasConstraints()
   {
      return constraintAnnotations.isEmpty() == false;
   }

   public Class<?> getElementClass()
   {
      return propertyType;
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
