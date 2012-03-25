package org.jboss.capedwarf.common.serialization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON transfomer.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface JSONTransformer<T> {
    /**
     * Set value.
     *
     * @param json json object
     * @param key the key
     * @param value the value
     * @throws JSONException for any error
     */
    void setValue(JSONObject json, String key, T value) throws JSONException;

    /**
     * Get value.
     *
     * @param json json object
     * @param key the key
     * @return value the value
     * @throws JSONException for any error
     */
    T getValue(JSONObject json, String key) throws JSONException;

    /**
     * Add value.
     *
     * @param array the array
     * @param value the value
     * @throws JSONException for any error
     */
    void addValue(JSONArray array, T value) throws JSONException;

    /**
     * Put value.
     *
     * @param array the array
     * @param index the index
     * @param value the value
     * @throws JSONException for any error
     */
    void putValue(JSONArray array, int index, T value) throws JSONException;

    /**
     * Get value.
     *
     * @param array the array
     * @param index the index
     * @return value the value
     * @throws JSONException for any error
     */
    T getValue(JSONArray array, int index) throws JSONException;
}
