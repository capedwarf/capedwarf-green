package org.jboss.capedwarf.connect.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Server query string.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query
{
   /**
    * Get the query value.
    *
    * @return the query
    */
   String value();

   /**
    * Is the query JSON aware.
    *
    * @return true if json aware, false otherwise
    */
   boolean jsonAware() default false;

   /**
    * Does query push content directly into stream.
    *
    * @return true if direct content streaming, false otherwise
    */
   boolean directContent() default false;
}
