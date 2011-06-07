package org.jboss.capedwarf.connect.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Server query parameter.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParameter
{
   /**
    * Get the query parameter name.
    *
    * @return the query
    */
   String value();

   /**
    * Is this parameter required.
    * By default it is true.
    *
    * @return true if required, false otherwise
    */
   boolean required() default true;
}
