package org.jboss.capedwarf.common.sql;

import java.lang.annotation.*;

/**
 * On insert.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OnInsert {
    /**
     * Get on insert action.
     *
     * @return the on load action.
     */
    String value();
}
