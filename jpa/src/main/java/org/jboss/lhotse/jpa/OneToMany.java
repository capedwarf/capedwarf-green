package org.jboss.lhotse.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.HashSet;

/**
 * Custom one-to-many.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OneToMany
{
   /**
    * The join field.
    *
    * @return the join pk field
    */
   String join() default "";

   /**
    * The element type.
    *
    * @return exact elemet type
    */
   Class<?> element() default Void.class;

   /**
    * The return collection type -- to simplify initialization.
    * By default we return HashSet.
    *
    * @return the collection type
    */
   Class<? extends Collection> type() default HashSet.class;

   /**
    * Order by.
    *
    * @return the order by
    */
   String orderBy() default "";
}
