package org.jboss.lhotse.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;

/**
 * javax.validation size validator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SimpleSizeConstraintValidator implements ConstraintValidator<Size, Object>
{
   private Size size;

   public void initialize(Size constraintAnnotation)
   {
      this.size = constraintAnnotation;
   }

   public boolean isValid(Object value, ConstraintValidatorContext context)
   {
      return ValidationHelper.doValidate(size,  value);
   }
}
