package org.jboss.lhotse.validation;

import javax.validation.ConstraintValidator;
import javax.validation.Payload;
import javax.validation.ValidationException;
import javax.validation.metadata.ConstraintDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * javax.validation constraint descriptor.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SimpleConstraintDescriptor<T extends Annotation> implements ConstraintDescriptor<T>
{
   private SimpleValidatorFactory factory;
   private T annotation;
   private Map<String, Object> attributes;

   SimpleConstraintDescriptor(SimpleValidatorFactory factory, T annotation)
   {
      if (factory == null)
         throw new IllegalArgumentException("Null factory");
      if (annotation == null)
         throw new IllegalArgumentException("Null annotation");

      this.annotation = annotation;
      this.attributes = buildAnnotationParameterMap();
   }

   public T getAnnotation()
   {
      return annotation;
   }

   public Set<Class<?>> getGroups()
   {
      return Collections.emptySet();
   }

   public Set<Class<? extends Payload>> getPayload()
   {
      return Collections.emptySet();
   }

   @SuppressWarnings({"unchecked"})
   public List<Class<? extends ConstraintValidator<T, ?>>> getConstraintValidatorClasses()
   {
      return new ArrayList(factory.getConstraintValidatorClasses());
   }

   public Map<String, Object> getAttributes()
   {
      return attributes;
   }

   public Set<ConstraintDescriptor<?>> getComposingConstraints()
   {
      return Collections.emptySet();
   }

   public boolean isReportAsSingleViolation()
   {
      return false;
   }

   private Map<String, Object> buildAnnotationParameterMap()
   {
      final Method[] declaredMethods;
      if (System.getSecurityManager() != null)
      {
         declaredMethods = AccessController.doPrivileged(new PrivilegedAction<Method[]>()
         {
            public Method[] run()
            {
               return annotation.annotationType().getDeclaredMethods();
            }
         });
      }
      else
      {
         declaredMethods = annotation.annotationType().getDeclaredMethods();
      }
      Map<String, Object> parameters = new HashMap<String, Object>(declaredMethods.length);
      for (Method m : declaredMethods)
      {
         try
         {
            parameters.put(m.getName(), m.invoke(annotation));
         }
         catch (IllegalAccessException e)
         {
            throw new ValidationException("Unable to read annotation attributes: " + annotation.getClass(), e);
         }
         catch (InvocationTargetException e)
         {
            throw new ValidationException("Unable to read annotation attributes: " + annotation.getClass(), e);
         }
      }
      return Collections.unmodifiableMap(parameters);
   }
}
