package org.jboss.capedwarf.validation;

import javax.validation.Configuration;
import javax.validation.ValidatorFactory;

import java.io.InputStream;

/**
 * Simple javax.validation configuration.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SimpleConfiguration extends ValidatorHolder<SimpleConfiguration> implements Configuration<SimpleConfiguration>
{
   public SimpleConfiguration ignoreXmlConfiguration()
   {
      return this;
   }

   public SimpleConfiguration addMapping(InputStream stream)
   {
      return this;
   }

   public SimpleConfiguration addProperty(String name, String value)
   {
      return this;
   }

   public ValidatorFactory buildValidatorFactory()
   {
      return new SimpleValidatorFactory(getDefaultMessageInterpolator(), getDefaultTraversableResolver(), getDefaultConstraintValidatorFactory());
   }
}
