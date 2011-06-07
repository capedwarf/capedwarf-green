package org.jboss.capedwarf.common.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SQL column.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column
{
   /**
    * Column name.
    *
    * @return the name
    */
   String name() default "";

   /**
    * Default value.
    *
    * @return the default value
    */
   String defaultValue() default "";
}
