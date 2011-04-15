package org.jboss.lhotse.validation;

import javax.validation.ValidatorFactory;

/**
 * javax.validation provider.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractValidatorFactoryProvider
{
   /**
    * Create factory.
    *
    * @return the validator factory
    */
   public abstract ValidatorFactory createFactory();

   /**
    * Get the validator factory impl.
    *
    * @return the factory impl
    */
   protected String getFactoryClassName()
   {
      return SimpleValidatorFactory.class.getName();
   }
}
