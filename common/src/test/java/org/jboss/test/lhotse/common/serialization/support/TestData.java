package org.jboss.test.lhotse.common.serialization.support;

import java.io.Serializable;

import org.jboss.lhotse.common.serialization.JSONAware;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class TestData implements JSONAware, Serializable
{
   private static final long serialVersionUID = 1l;
   
   private long id;
   private String topic;
   private double x;
   private String extra;

   public void writeJSONObject(JSONObject json) throws JSONException
   {
      json.put("id", id);
      json.put("topic", topic);
      json.put("x", x);
      json.putOpt("extra", extra);
   }

   public void readJSONObject(JSONObject json) throws JSONException
   {
      id = json.getLong("id");
      topic = json.getString("topic");
      x = json.getDouble("x");
      extra = json.optString("extra");
   }

   public long getId()
   {
      return id;
   }

   public void setId(long id)
   {
      this.id = id;
   }

   public String getTopic()
   {
      return topic;
   }

   public void setTopic(String topic)
   {
      this.topic = topic;
   }

   public double getX()
   {
      return x;
   }

   public void setX(double x)
   {
      this.x = x;
   }

   public String getExtra()
   {
      return extra;
   }

   public void setExtra(String extra)
   {
      this.extra = extra;
   }

   @Override
   public int hashCode()
   {
      return topic.hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof TestData == false)
         return false;

      TestData td = (TestData) obj;
      return (id == td.id) && (x == td.x) && (topic.equals(td.topic));  
   }
}
