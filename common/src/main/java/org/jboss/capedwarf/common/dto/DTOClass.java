package org.jboss.capedwarf.common.dto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark dto class.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DTOClass
{
   /**
    * The dto mapped class.
    *
    * @return the dto mapped class
    */
   Class<?> value() default Void.class;

   /**
    * DTO model class.
    *
    * @return the dto model class
    */
   Class<? extends DTOModel> model() default DefaultDTOModel.class;

   /**
    * Do we need to pass class name.
    *
    * @return true if we need to pass the class name, false otherwise
    */
   boolean needsClassName() default false;
}
