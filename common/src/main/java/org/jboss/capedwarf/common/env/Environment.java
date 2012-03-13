package org.jboss.capedwarf.common.env;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.logging.Level;

/**
 * The environment utils.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface Environment {
    /**
     * Touch the env -- so we see it's functioning.
     */
    void touch();

    /**
     * Log.
     *
     * @param category the category
     * @param level    the level
     * @param msg      the msg
     * @param t        the throwable
     */
    void log(String category, Level level, String msg, Throwable t);

    /**
     * Get environment type.
     *
     * @return the env type
     */
    EnvironmentType envType();

    /**
     * Create json object.
     *
     * @return new json object instance
     */
    JSONObject createObject();

    /**
     * Create tokener.
     *
     * @param is the input stream
     * @return new json tokener
     * @throws IOException for any I/O error
     */
    JSONTokener createTokener(InputStream is) throws IOException;

    /**
     * Write json object.
     *
     * @param object the object
     * @param writer the writer
     * @throws JSONException for any json exception
     */
    void writeObject(JSONObject object, Writer writer) throws JSONException;

    /**
     * Write json array.
     *
     * @param array  the array
     * @param writer the writer
     * @throws JSONException for any json exception
     */
    void writeArray(JSONArray array, Writer writer) throws JSONException;

    /**
     * Get user id.
     *
     * @return the user id
     */
    long getUserId();

    /**
     * Get user token.
     *
     * @return the user token
     */
    String getUserToken();
}
