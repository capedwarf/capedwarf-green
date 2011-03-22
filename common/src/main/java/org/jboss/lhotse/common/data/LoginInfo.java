package org.jboss.lhotse.common.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple login token.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class LoginInfo extends StatusInfo
{
   private static final long serialVersionUID = 1l;

   private String token;
   private String username;

   public LoginInfo()
   {
      // only to be used with deserialization
   }

   public LoginInfo(Status status)
   {
      this(status, 0, null);   
   }

   public LoginInfo(Status status, String token)
   {
      this(status, 0, token);
   }

   public LoginInfo(Status status, long id, String token)
   {
      super(status, id);
      this.token = token;
   }

   public void writeJSONObject(JSONObject json) throws JSONException
   {
      super.writeJSONObject(json);
      json.putOpt("token", token);
      json.putOpt("username", username);
   }

   public void readJSONObject(JSONObject json) throws JSONException
   {
      super.readJSONObject(json);
      token = json.optString("token");
      username = json.optString("username");
   }

   public String getToken()
   {
      return token;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public String toShortString()
   {
      return "LoginInfo [id: " + getId() + ", status: " + getStatus() + ", token: " + token + "]";
   }
}
