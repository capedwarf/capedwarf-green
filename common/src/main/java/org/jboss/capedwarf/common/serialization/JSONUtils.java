package org.jboss.capedwarf.common.serialization;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON utils.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JSONUtils {
    /**
     * Create JSON exception.
     *
     * @param t the throwable
     * @return new JSON exception
     */
    public static JSONException createJSONException(Throwable t) {
        if (t == null)
            return new JSONException("Undeclared JSON exception.");
        else {
            JSONException e = new JSONException("Converting to JSON exception: " + t.getMessage());
            e.initCause(t);
            return e;
        }
    }

    /**
     * Write bytes.
     *
     * @param json  the json object to write to
     * @param key   the key
     * @param bytes the bytes
     * @throws JSONException for any JSON error
     */
    public static void writeArray(JSONObject json, String key, byte[] bytes) throws JSONException {
        if (json == null)
            throw new IllegalArgumentException("Null JSON object");
        if (key == null)
            throw new IllegalArgumentException("Null key");

        if (bytes != null && bytes.length > 0) {
            String value = Hack64.byteArrayToBase64(bytes);
            json.put(key, value);
        }
    }

    /**
     * Read bytes.
     *
     * @param json the json object to read from
     * @param key  the key
     * @return bytes or null if no such value
     * @throws JSONException for any JSON error
     */
    public static byte[] readArray(JSONObject json, String key) throws JSONException {
        if (json == null)
            throw new IllegalArgumentException("Null JSON object");
        if (key == null)
            throw new IllegalArgumentException("Null key");

        String value = json.optString(key);
        if (value != null) {
            return Hack64.base64ToByteArray(value);
        } else {
            return null;
        }
    }

    /**
     * Write objects.
     *
     * @param json    the json object to write to
     * @param key     the key
     * @param objects the objects
     * @throws JSONException for any JSON error
     */
    public static void writeObjects(JSONObject json, String key, Iterable<? extends JSONAware> objects) throws JSONException {
        if (json == null)
            throw new IllegalArgumentException("Null JSON object");
        if (key == null)
            throw new IllegalArgumentException("Null key");

        if (objects != null) {
            JSONArray array = new JSONArray();
            for (JSONAware ja : objects) {
                JSONObject object = new JSONObject();
                ja.writeJSONObject(object);
                array.put(object);
            }
            json.put(key, array);
        }
    }

    /**
     * Read objects.
     *
     * @param json  the json object to read from
     * @param key   the key
     * @param clazz the json aware class
     * @return objects or null if no such value
     * @throws JSONException for any JSON error
     */
    public static <T extends JSONAware> List<T> readObjects(JSONObject json, String key, Class<T> clazz) throws JSONException {
        if (json == null)
            throw new IllegalArgumentException("Null JSON object");
        if (key == null)
            throw new IllegalArgumentException("Null key");
        if (clazz == null)
            throw new IllegalArgumentException("Null JSONAware class");

        JSONArray array = json.optJSONArray(key);
        if (array != null) {
            int n = array.length();
            List<T> list = new ArrayList<T>(n);
            for (int i = 0; i < n; i++) {
                T instance;
                try {
                    instance = clazz.newInstance();
                } catch (Exception e) {
                    throw createJSONException(e);
                }
                JSONObject object = array.getJSONObject(i);
                instance.readJSONObject(object);
                list.add(instance);
            }
            return list;
        } else {
            return null;
        }
    }

    /**
     * Write object.
     *
     * @param json the json object to write to
     * @param key  the key
     * @param ja   the JSON aware object
     * @throws JSONException for any JSON error
     */
    public static void writeObject(JSONObject json, String key, JSONAware ja) throws JSONException {
        if (json == null)
            throw new IllegalArgumentException("Null JSON object");
        if (key == null)
            throw new IllegalArgumentException("Null key");

        if (ja != null) {
            JSONObject object = new JSONObject();
            ja.writeJSONObject(object);
            json.put(key, object);
        }
    }

    /**
     * Read object.
     *
     * @param json  the json object to read from
     * @param key   the key
     * @param clazz the json aware class
     * @return object or null if no such value
     * @throws JSONException for any JSON error
     */
    public static <T extends JSONAware> T readObject(JSONObject json, String key, Class<T> clazz) throws JSONException {
        if (json == null)
            throw new IllegalArgumentException("Null JSON object");
        if (key == null)
            throw new IllegalArgumentException("Null key");
        if (clazz == null)
            throw new IllegalArgumentException("Null JSONAware class");


        JSONObject object = json.optJSONObject(key);
        if (object != null) {
            T instance;
            try {
                instance = clazz.newInstance();
            } catch (Exception e) {
                throw createJSONException(e);
            }
            instance.readJSONObject(object);
            return instance;
        } else {
            return null;
        }
    }

    /**
     * Write enum.
     *
     * @param json  the json object to read from
     * @param key   the key
     * @param value the enum vakue
     * @throws JSONException for any JSON error
     */
    public static void writeEnum(JSONObject json, String key, Enum value) throws JSONException {
        if (value != null) {
            json.put(key, value.name());
        }
    }

    /**
     * Read enum.
     *
     * @param json         the json object to read from
     * @param key          the key
     * @param defaultValue the default value
     * @param enumClass    the enum class
     * @return matching enum or default value
     * @throws JSONException for any JSON error
     */
    public static <T extends Enum<T>> T readEnum(JSONObject json, String key, T defaultValue, Class<T> enumClass) throws JSONException {
        String name = json.optString(key);
        return (name != null) ? Enum.valueOf(enumClass, name) : defaultValue;
    }
}
