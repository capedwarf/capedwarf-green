package org.jboss.lhotse.common.validation;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Size;

import java.lang.reflect.Array;
import java.util.Set;

/**
 * Validation helper.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ValidationHelper
{
   private static ValidatorFactory factory = new ValidatorFactoryProvider().createFactory();

   /**
    * Create validator.
    *
    * @return new validator
    */
   public static Validator createValidator()
   {
      return factory.getValidator();
   }

   @SuppressWarnings("unchecked")
   public static void validate(Object arg) throws Exception
   {
      Validator validator = createValidator();
      Set violations = validator.validate(arg);
      if (violations.isEmpty() == false)
         throw new ConstraintViolationException("Invalid arg: " + arg, violations);
   }

   /**
    * Validate argument wrt size.
    *
    * @param size the size
    * @param arg the argument
    */
   public static void validate(Size size, Object arg)
   {
      if (doValidate(size, arg) == false)
         throw new ConstraintViolationException("Illegal size : " + arg, null);
   }

   /**
    * Do validate.
    *
    * @param size the size
    * @param arg the argument
    * @return true if valid, false otherwise
    */
   public static boolean doValidate(Size size, Object arg)
   {
      if (arg == null)
         return true;

      if (arg instanceof Number)
      {
         Number number = (Number) arg;
         return validate(size, number.intValue());
      }
      else if (arg instanceof String)
      {
         String string = (String) arg;
         return validate(size, string.length());
      }
      else if (arg.getClass().isArray())
      {
         return validate(size, Array.getLength(arg));
      }
      return true;
   }

   private static boolean validate(Size size, int x)
   {
      int min = size.min();
      int max = size.max();

      return (x >= min && x <= max);
   }
}
