package org.jboss.lhotse.common.validation;

import javax.validation.Configuration;
import javax.validation.ValidatorFactory;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;
import javax.validation.spi.ValidationProvider;

/**
 * Simple validation provider.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleValidationProvider implements ValidationProvider<SimpleConfiguration>
{
   public Configuration<?> createGenericConfiguration(BootstrapState state)
   {
      return createSpecializedConfiguration(state);
   }

   public SimpleConfiguration createSpecializedConfiguration(BootstrapState state)
   {
      return new SimpleConfiguration();
   }

   public ValidatorFactory buildValidatorFactory(ConfigurationState configurationState)
   {
      return new SimpleValidatorFactory();
   }
}
