package org.jboss.capedwarf.common.sql;

import java.lang.annotation.*;

/**
 * On update.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OnUpdate {
    /**
     * Get on update action.
     *
     * @return the on load action.
     */
    String value();
}
