package org.jboss.capedwarf.common.data;

import javax.validation.constraints.Size;

import org.jboss.capedwarf.common.dto.Timestamped;
import org.jboss.capedwarf.common.serialization.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ales Justin
 */
public class PhotoInfo extends Timestamped
{
   private static final long serialVersionUID = 1l;

   private transient byte[] photo; // if used as part of EventInfo, this is the its BIG image

   public void writeJSONObject(JSONObject json) throws JSONException
   {
      super.writeJSONObject(json);
      JSONUtils.writeArray(json, "photo", photo);
   }

   public void readJSONObject(JSONObject json) throws JSONException
   {
      super.readJSONObject(json);
      photo = JSONUtils.readArray(json, "photo");
   }

   public Long getEventId()
   {
      return getId();
   }

   public void setEventId(Long eventId)
   {
      setId(eventId);
   }

   @Size(max = 1000000)
   public byte[] getPhoto()
   {
      return photo;
   }

   public void setPhoto(byte[] photo)
   {
      this.photo = photo;
   }
}
