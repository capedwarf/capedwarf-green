package org.jboss.lhotse.common.env;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * The GAE environment utils.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class GAEEnvironment implements Environment
{
   private static long userId = 0;
   private static String userToken = "dummy-token";
   
   static void setUserId(long id)
   {
      userId = id;
   }

   static void setUserToken(String token)
   {
      userToken = token;
   }

   public void touch()
   {
      // ignore
   }

   public void log(String category, Level level, String msg, Throwable t)
   {
      Logger log = Logger.getLogger(category);
      if (t == null)
         log.log(level, msg);
      else
         log.log(level, msg, t);
   }

   public EnvironmentType envType()
   {
      return EnvironmentType.GAE;
   }

   public JSONObject createObject()
   {
      return new JSONObject();
   }

   public JSONTokener createTokener(InputStream is)
   {
      try
      {
         return new JSONTokener(new InputStreamReader(is, "UTF-8"));
      }
      catch (UnsupportedEncodingException ignored)
      {
         throw new RuntimeException("ASSERTION FAILED: No UTF-8 support");
      }
   }

   public void writeObject(JSONObject object, Writer writer) throws JSONException
   {
      object.write(writer);
   }

   public void writeArray(JSONArray array, Writer writer) throws JSONException
   {
      array.write(writer);
   }

   public long getUserId()
   {
      return userId;
   }

   public String getUserToken()
   {
      return userToken;
   }
}
