package org.jboss.capedwarf.common.dto;

import org.jboss.capedwarf.common.sql.Column;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple timestamped dto.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class Timestamped extends Identity {
    private static final long serialVersionUID = 1l;

    // No of miliseconds since 1970 1.1. UTC
    private long timestamp;
    // Expiration time in milis
    private long expirationDelta;

    /**
     * Is the dto expired.
     *
     * @return true if expired, false otherwise.
     */
    public boolean isExpired() {
        return (timestamp + expirationDelta < System.currentTimeMillis());
    }

    public void writeJSONObject(JSONObject json) throws JSONException {
        super.writeJSONObject(json);
        if (timestamp > 0)
            json.put("timestamp", timestamp);
        if (expirationDelta > 0)
            json.put("expirationDelta", expirationDelta);
    }

    public void readJSONObject(JSONObject json) throws JSONException {
        super.readJSONObject(json);
        timestamp = json.optLong("timestamp", 0);
        expirationDelta = json.optLong("expirationDelta", 0);
    }

    @Column
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Column
    public long getExpirationDelta() {
        return expirationDelta;
    }

    public void setExpirationDelta(long expirationDelta) {
        this.expirationDelta = expirationDelta;
    }

    public boolean equals(Object obj) {
        if (super.equals(obj) == false)
            return false;

        Timestamped other = (Timestamped) obj;
        return (timestamp == other.timestamp && expirationDelta == other.expirationDelta);
    }
}
