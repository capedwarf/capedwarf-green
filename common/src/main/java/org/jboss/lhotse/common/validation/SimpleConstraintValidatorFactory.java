package org.jboss.lhotse.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

/**
 * Simple javax.validation constraint validator factory.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SimpleConstraintValidatorFactory implements ConstraintValidatorFactory
{
   static ConstraintValidatorFactory INSTANCE = new SimpleConstraintValidatorFactory();

   public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key)
   {
      try
      {
         return key.newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
