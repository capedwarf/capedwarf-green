package org.jboss.capedwarf.common.sql;

import java.io.Serializable;

/**
 * SQL object / entity.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SQLObject implements Serializable {
    private static final long serialVersionUID = 1l;

    /**
     * Client side key
     */
    private Long pk;

    @Key("pk")
    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        if (getClass().equals(obj.getClass()) == false)
            return false;

        SQLObject other = (SQLObject) obj;

        if (pk == null && other.pk == null)
            return true; // leave it up to sub-classes to implement it properly!
        if ((pk == null && other.pk != null) || (pk != null && other.pk == null))
            return false;

        return (pk - other.pk == 0);
    }
}
