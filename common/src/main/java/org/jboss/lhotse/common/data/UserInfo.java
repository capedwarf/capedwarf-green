package org.jboss.lhotse.common.data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.io.Serializable;

import org.jboss.lhotse.common.Constants;
import org.jboss.lhotse.common.serialization.JSONAware;
import org.jboss.lhotse.common.serialization.JSONUtils;
import org.jboss.lhotse.validation.Email;
import org.jboss.lhotse.validation.MessageTemplateKey;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User info
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class UserInfo implements JSONAware, Serializable
{
   private static final long serialVersionUID = 1l;

   private String username;
   private String password;
   private String email;
   private Status status;
   private String recovery;

   @Deprecated
   public UserInfo()
   {
      // deserialization only
   }

   public UserInfo(String username, String password)
   {
      if (username == null)
         throw new IllegalArgumentException("Null username");
      if (password == null)
         throw new IllegalArgumentException("Null password");

      this.username = username;
      this.password = password;
   }

   public void writeJSONObject(JSONObject json) throws JSONException
   {
      json.put("username", username);
      json.put("password", password);
      json.putOpt("email", email);
      JSONUtils.writeEnum(json, "status", status);
      json.putOpt("recovery", recovery);
   }

   public void readJSONObject(JSONObject json) throws JSONException
   {
      username = json.getString("username");
      password = json.getString("password");
      email = json.optString("email");
      status = JSONUtils.readEnum(json, "status", null, Status.values());
      recovery = json.optString("recovery");
   }

   @Size(min = 3, max = 20)
   @Pattern(regexp = Constants.USERNAME_REGEXP)
   @MessageTemplateKey("{lhotse.login.username}")
   public String getUsername()
   {
      return username;
   }

   @Size(min = 6, max = 30)
   @Pattern(regexp = Constants.PASSWORD_REGEXP)
   @MessageTemplateKey("{lhotse.login.password}")
   public String getPassword()
   {
      return password;
   }

   @Email
   @MessageTemplateKey("{lhotse.login.email}")
   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   public Status getStatus()
   {
      return status;
   }

   public void setStatus(Status status)
   {
      this.status = status;
   }

   @MessageTemplateKey("{lhotse.login.recovery}")
   public String getRecovery()
   {
      return recovery;
   }

   public void setRecovery(String recovery)
   {
      this.recovery = recovery;
   }
}