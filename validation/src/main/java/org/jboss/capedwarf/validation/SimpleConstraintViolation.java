package org.jboss.capedwarf.validation;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Iterator;

/**
 * javax.validation constraint violation.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SimpleConstraintViolation<T> implements ConstraintViolation<T>
{
   private SimpleValidatorFactory factory;
   private String message;
   private T rootBean;
   private Class<T> rootBeanClass;
   private Annotation annotation;
   private String propertyPath;
   private Object invalidValue;

   SimpleConstraintViolation(SimpleValidatorFactory factory, String message, T rootBean, Class<T> rootBeanClass, Annotation annotation, String propertyPath, Object invalidValue)
   {
      this.factory = factory;
      this.message = message;
      this.rootBean = rootBean;
      this.rootBeanClass = rootBeanClass;
      this.annotation = annotation;
      this.propertyPath = propertyPath;
      this.invalidValue = invalidValue;
   }

   public String getMessage()
   {
      return message;
   }

   public String getMessageTemplate()
   {
      return null;
   }

   public T getRootBean()
   {
      return rootBean;
   }

   public Class<T> getRootBeanClass()
   {
      return rootBeanClass;
   }

   public Object getLeafBean()
   {
      return null;
   }

   public Path getPropertyPath()
   {
      return new Path()
      {
         public Iterator<Node> iterator()
         {
            return Collections.<Node>singleton(new Node()
            {
               public String getName()
               {
                  return propertyPath;
               }

               public boolean isInIterable()
               {
                  return false;
               }

               public Integer getIndex()
               {
                  return 0;
               }

               public Object getKey()
               {
                  return null;
               }
            }).iterator();
         }
      };
   }

   public Object getInvalidValue()
   {
      return invalidValue;
   }

   public ConstraintDescriptor<?> getConstraintDescriptor()
   {
      return new SimpleConstraintDescriptor<Annotation>(factory, annotation);
   }

   public String toString()
   {
      StringBuilder builder = new StringBuilder("SimpleConstraintViolation: ");
      builder.append("msg=").append(message);
      builder.append(", bean=").append(rootBean);
      builder.append(", annotation=").append(annotation);
      builder.append(", property=").append(propertyPath);
      builder.append(", invalid-value=").append(invalidValue);
      return builder.toString();
   }
}
