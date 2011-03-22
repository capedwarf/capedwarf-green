package org.jboss.lhotse.common.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark dto property.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DTOProperty
{
   /**
    * The matching dto property.
    *
    * @return the dto property name
    */
   String property() default "";

   /**
    * Convert value.
    *
    * @return the value converter class
    */
   Class<? extends ValueConverter> converter() default NoopValueConverter.class;
}
