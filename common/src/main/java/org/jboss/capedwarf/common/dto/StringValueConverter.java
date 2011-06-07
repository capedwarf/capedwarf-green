package org.jboss.capedwarf.common.dto;

/**
 * Noop value converter.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class StringValueConverter<R> implements ValueConverter<String, R>
{
   public static final StringValueConverter<Object> INSTANCE = new StringValueConverter<Object>();
   
   public String convert(R value)
   {
      return String.valueOf(value);
   }
}
