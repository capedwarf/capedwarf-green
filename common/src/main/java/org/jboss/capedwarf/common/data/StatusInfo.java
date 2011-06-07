package org.jboss.capedwarf.common.data;

import java.io.Serializable;

import org.jboss.capedwarf.common.serialization.JSONAware;
import org.jboss.capedwarf.common.serialization.JSONUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple status info.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class StatusInfo implements JSONAware, Serializable
{
   private static final long serialVersionUID = 1l;

   private Status status;
   private long id;
   private long timestamp;

   public StatusInfo()
   {
      // only to be used with deserialization
   }

   public StatusInfo(Status status)
   {
      this(status, 0);
   }

   public StatusInfo(Status status, long id)
   {
      if (status == null)
         throw new IllegalArgumentException("Null status");

      this.status = status;
      this.id = id;
   }

   public void writeJSONObject(JSONObject json) throws JSONException
   {
      if (status == null)
         throw new IllegalArgumentException("Null status, wrong ctor usage?");

      JSONUtils.writeEnum(json, "status", status);
      if (id > 0)
         json.put("id", id);
      if (timestamp > 0)
         json.put("timestamp", timestamp);
   }

   public void readJSONObject(JSONObject json) throws JSONException
   {
      Status tmp = JSONUtils.readEnum(json, "status", null, Status.values());
      if (tmp == null)
         throw new IllegalArgumentException("Null status, wrong ctor usage?");

      status = tmp;
      id = json.optLong("id");
      timestamp = json.optLong("timestamp");
   }

   public Status getStatus()
   {
      return status;
   }

   /**
    * Get id.
    * Can be 0, indicating it's illegal.
    *
    * @return the id
    */
   public long getId()
   {
      return id;
   }

   /**
    * Get server side timestamp.
    * Can be zero -- meaning no server side set was done.
    *
    * @return the timestamp or zero if not set
    */
   public long getTimestamp()
   {
      return timestamp;
   }

   public void setTimestamp(long timestamp)
   {
      this.timestamp = timestamp;
   }

   public String toString()
   {
      return "status: " + status + ", id = " + id;
   }
}
