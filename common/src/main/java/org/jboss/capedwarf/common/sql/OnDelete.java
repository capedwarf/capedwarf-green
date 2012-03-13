package org.jboss.capedwarf.common.sql;

import java.lang.annotation.*;

/**
 * On delete.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OnDelete {
    /**
     * Get on delete action.
     *
     * @return the on delete action.
     */
    String value();
}
