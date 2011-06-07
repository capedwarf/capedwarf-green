package org.jboss.capedwarf.validation;

import javax.validation.Validator;
import javax.validation.ValidatorContext;

/**
 * javax.validation validator context.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class SimpleValidatorContext extends ValidatorHolder<SimpleValidatorContext> implements ValidatorContext
{
   private SimpleValidatorFactory factory;

   SimpleValidatorContext(SimpleValidatorFactory factory)
   {
      this.factory = factory;
   }

   public Validator getValidator()
   {
      return new SimpleValidator(factory, getMessageInterpolator(), getTraversableResolver(), getConstraintValidatorFactory());
   }
}
