package org.jboss.capedwarf.common.serialization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON transfomers.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class JSONTransformers {
    public static final JSONTransformer<String> STRING = new JSONTransformer<String>() {
        public void setValue(JSONObject json, String key, String value) throws JSONException {
            if (value != null)
                json.put(key, value);
        }

        public String getValue(JSONObject json, String key) throws JSONException {
            return json.isNull(key) ? null : json.getString(key);
        }

        public void addValue(JSONArray array, String value) throws JSONException {
            if (value != null)
                array.put(value);
        }

        public void putValue(JSONArray array, int index, String value) throws JSONException {
            if (value != null)
                array.put(index, value);
        }

        public String getValue(JSONArray array, int index) throws JSONException {
            return array.isNull(index) ? null : array.getString(index);
        }
    };

    public static final JSONTransformer<Long> LONG = new JSONTransformer<Long>() {
        public void setValue(JSONObject json, String key, Long value) throws JSONException {
            if (value != null)
                json.put(key, (long) value);
        }

        public Long getValue(JSONObject json, String key) throws JSONException {
            return json.isNull(key) ? null : json.getLong(key);
        }

        public void addValue(JSONArray array, Long value) throws JSONException {
            if (value != null)
                array.put((long) value);
        }

        public void putValue(JSONArray array, int index, Long value) throws JSONException {
            if (value != null)
                array.put(index, (long) value);
        }

        public Long getValue(JSONArray array, int index) throws JSONException {
            return array.isNull(index) ? null : array.getLong(index);
        }
    };

    public static final JSONTransformer<Double> DOUBLE = new JSONTransformer<Double>() {
        public void setValue(JSONObject json, String key, Double value) throws JSONException {
            if (value != null)
                json.put(key, (double) value);
        }

        public Double getValue(JSONObject json, String key) throws JSONException {
            return json.isNull(key) ? null : json.getDouble(key);
        }

        public void addValue(JSONArray array, Double value) throws JSONException {
            if (value != null)
                array.put((double) value);
        }

        public void putValue(JSONArray array, int index, Double value) throws JSONException {
            if (value != null)
                array.put(index, (double) value);
        }

        public Double getValue(JSONArray array, int index) throws JSONException {
            return array.isNull(index) ? null : array.getDouble(index);
        }
    };

    public static final JSONTransformer<Boolean> BOOLEAN = new JSONTransformer<Boolean>() {
        public void setValue(JSONObject json, String key, Boolean value) throws JSONException {
            if (value != null)
                json.put(key, (boolean) value);
        }

        public Boolean getValue(JSONObject json, String key) throws JSONException {
            return json.isNull(key) ? null : json.getBoolean(key);
        }

        public void addValue(JSONArray array, Boolean value) throws JSONException {
            if (value != null)
                array.put((boolean) value);
        }

        public void putValue(JSONArray array, int index, Boolean value) throws JSONException {
            if (value != null)
                array.put(index, (boolean) value);
        }

        public Boolean getValue(JSONArray array, int index) throws JSONException {
            return array.isNull(index) ? null : array.getBoolean(index);
        }
    };
}
