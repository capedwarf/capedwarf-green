package org.jboss.lhotse.validation;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Validate entities.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ValidationListener
{
   private static Logger log = Logger.getLogger(ValidationListener.class.getName());
   private static ValidatorFactory factory;

   private boolean warning;

   @SuppressWarnings("unchecked")
   public void validate(Object object) throws Exception
   {
      if (factory == null)
      {
         if (warning == false)
         {
            log.warning("No ValidatorFactory set!");
            warning = true;
         }
         return;
      }

      Validator validator = factory.getValidator();
      Set violations = validator.validate(object);
      if (violations.isEmpty() == false)
      {
         log.log(Level.INFO, "Constraint violations: " + violations.toString());
         throw new ConstraintViolationException("Invalid object: " + object, violations);
      }
   }

   public static void setFactory(ValidatorFactory vf)
   {
      if (vf == null)
         throw new IllegalArgumentException("Null factory");
      factory = vf;
   }
}
