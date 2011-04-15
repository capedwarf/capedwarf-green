package org.jboss.lhotse.validation.api;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotNull;

/**
 * javax.validation not-null validator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class NotNullConstraintValidator implements ConstraintValidator<NotNull, Object>
{
   public void initialize(NotNull constraintAnnotation)
   {
   }

   public boolean isValid(Object value, ConstraintValidatorContext context)
   {
      return (value != null);
   }
}
