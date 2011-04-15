package org.jboss.lhotse.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * javax.validation pattern validator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractPatternConstraintValidator<T extends Annotation> implements ConstraintValidator<T, Object>
{
   private Pattern pattern;

   /**
    * Create pattern from annotation.
    *
    * @param constraintAnnotation the constraint annotation
    * @return new pattern
    */
   protected abstract Pattern createPattern(T constraintAnnotation);

   public void initialize(T constraintAnnotation)
   {
      pattern = createPattern(constraintAnnotation);
   }

   /**
    * Check lenght.
    *
    * @param string the string to check
    * @return if null is returned we check the pattern, else return the boolean value
    */
   protected Boolean lengthCheck(String string)
   {
      return null;
   }

   public boolean isValid(Object value, ConstraintValidatorContext context)
   {
      if (value == null)
         return true;

      if (value instanceof String == false)
         return false;

      String string = (String) value;
      Boolean check = lengthCheck(string);
      if (check != null)
         return check;

      Matcher m = pattern.matcher(string);
      return m.matches();
   }
}
