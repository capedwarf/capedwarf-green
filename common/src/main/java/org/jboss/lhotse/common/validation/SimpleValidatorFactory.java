package org.jboss.lhotse.common.validation;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * javax.validation validator factory.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleValidatorFactory extends ValidatorHolder<SimpleValidatorFactory> implements ValidatorFactory
{
   private Map<Class<? extends Annotation>, Class<? extends ConstraintValidator<? extends Annotation, Object>>> constraints = new WeakHashMap<Class<? extends Annotation>, Class<? extends ConstraintValidator<? extends Annotation, Object>>>();
   private Map<Class<?>, SimpleBeanDescriptor> descriptors = new ConcurrentHashMap<Class<?>, SimpleBeanDescriptor>();

   public SimpleValidatorFactory()
   {
      init();
   }

   public SimpleValidatorFactory(MessageInterpolator messageInterpolator, TraversableResolver traversableResolver, ConstraintValidatorFactory constraintValidatorFactory)
   {
      super(messageInterpolator, traversableResolver, constraintValidatorFactory);
      init();
   }

   private void init()
   {
      put(NotNull.class, NotNullConstraintValidator.class);
      put(Size.class, SimpleSizeConstraintValidator.class);
      put(Email.class, EmailConstraintValidator.class);
      put(Pattern.class, PatternConstraintValidator.class);
   }

   List<Class<? extends ConstraintValidator>> getConstraintValidatorClasses()
   {
      return new ArrayList<Class<? extends ConstraintValidator>>(constraints.values());
   }

   public Validator getValidator()
   {
      return new SimpleValidator(this);
   }

   Set<Class<? extends Annotation>> getConstraintKeys()
   {
      return constraints.keySet();
   }

   ConstraintValidator<? extends Annotation, Object> getConstraintValidator(Class<? extends Annotation> ac)
   {
      if (ac == null)
         throw new IllegalArgumentException("Null annotation");

      Class<? extends ConstraintValidator<? extends Annotation, Object>> validatorClass = get(ac);
      if (validatorClass == null)
         return null;

      try
      {
         return validatorClass.newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public Class<? extends ConstraintValidator<? extends Annotation, Object>> get(Class<? extends Annotation> clazz)
   {
      return constraints.get(clazz);
   }

   public void put(Class<? extends Annotation> clazz, Class<? extends ConstraintValidator<? extends Annotation, Object>> cv)
   {
      constraints.put(clazz, cv);
   }

   public void remove(Class<? extends Annotation> clazz)
   {
      constraints.remove(clazz);
   }

   SimpleBeanDescriptor getBeanDescriptor(Class<?> beanClass)
   {
      SimpleBeanDescriptor bc = descriptors.get(beanClass);
      if (bc == null)
      {
         bc = new SimpleBeanDescriptor(this, beanClass);
         descriptors.put(beanClass, bc);
      }
      return bc;
   }

   public ValidatorContext usingContext()
   {
      return new SimpleValidatorContext(this);
   }

   public <T> T unwrap(Class<T> type)
   {
      return null;
   }
}
