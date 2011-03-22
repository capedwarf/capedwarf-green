package org.jboss.lhotse.common.serialization;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON aware class
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface JSONAware
{
   /**
    * Write JSON object.
    *
    * @param json the JSON object
    * @throws JSONException for any JSON error
    */
   void writeJSONObject(JSONObject json) throws JSONException;

   /**
    * Read from JSON object.
    * 
    * @param json the JSON object
    * @throws JSONException for any JSON error
    */
   void readJSONObject(JSONObject json) throws JSONException;
}