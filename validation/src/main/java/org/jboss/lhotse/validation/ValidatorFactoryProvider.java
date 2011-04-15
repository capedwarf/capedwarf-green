package org.jboss.lhotse.validation;

import javax.validation.ValidatorFactory;

/**
 * javax.validation provider.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ValidatorFactoryProvider extends AbstractValidatorFactoryProvider
{
   public ValidatorFactory createFactory()
   {
      try
      {
         Class clazz = Class.forName(getFactoryClassName(), false, ValidatorFactory.class.getClassLoader());
         return (ValidatorFactory) clazz.newInstance();
      }
      catch (Throwable t)
      {
         throw new RuntimeException(t);
      }
   }
}
