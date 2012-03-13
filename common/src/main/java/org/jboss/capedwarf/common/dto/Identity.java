package org.jboss.capedwarf.common.dto;

import org.jboss.capedwarf.common.serialization.JSONAware;
import org.jboss.capedwarf.common.sql.Column;
import org.jboss.capedwarf.common.sql.SQLObject;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple identity dto.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class Identity extends SQLObject implements JSONAware {
    private static final long serialVersionUID = 1l;

    /**
     * Server side id
     */
    private Long id;
    private String className;

    @Column
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void writeJSONObject(JSONObject json) throws JSONException {
        json.putOpt("id", id);
        if (className != null)
            json.put("className", className);
    }

    public void readJSONObject(JSONObject json) throws JSONException {
        id = json.optLong("id");
        className = json.optString("className");
    }

    public boolean equals(Object obj) {
        if (super.equals(obj) == false)
            return false;

        if (this == obj)
            return true;

        Identity other = (Identity) obj;

        // if it's not same instance - previous check - we return false,
        // as we presume null id's mean diff instance
        if (id == null && other.id == null)
            return false;

        if ((id == null && other.id != null) || (id != null && other.id == null))
            return false;

        return (id - other.id == 0);
    }
}
