package org.jboss.test.capedwarf.validation.support;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.jboss.capedwarf.validation.api.Constants;
import org.jboss.capedwarf.validation.api.Email;
import org.jboss.capedwarf.validation.api.MessageTemplateKey;


/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DummyInfo
{
   private String string;
   private byte[] bytes;
   private String email;
   private String username;

   @NotNull
   @Size(min = 1, max = 3)
   public String getString()
   {
      return string;
   }

   public void setString(String string)
   {
      this.string = string;
   }

   @NotNull
   @Size(min = 1, max = 3)
   public byte[] getBytes()
   {
      return bytes;
   }

   public void setBytes(byte[] bytes)
   {
      this.bytes = bytes;
   }

   @Email
   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   @Pattern(regexp = Constants.PASSWORD_REGEXP)
   @MessageTemplateKey("{invalid.username}")
   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }
}
