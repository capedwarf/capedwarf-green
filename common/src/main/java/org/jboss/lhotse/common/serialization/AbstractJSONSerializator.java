package org.jboss.lhotse.common.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.jboss.lhotse.common.env.EnvironmentFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * JSON based serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractJSONSerializator extends AbstractSerializator
{
   protected JSONObject createObject() throws IOException
   {
      return EnvironmentFactory.getEnvironment().createObject();
   }

   protected JSONTokener createTokener(InputStream is) throws IOException
   {
      return EnvironmentFactory.getEnvironment().createTokener(is);
   }

   protected void writeObject(JSONObject object, Writer writer) throws JSONException
   {
      EnvironmentFactory.getEnvironment().writeObject(object, writer);
   }

   protected void writeArray(JSONArray array, Writer writer) throws JSONException
   {
      EnvironmentFactory.getEnvironment().writeArray(array, writer);
   }

   public void serialize(Object instance, OutputStream out) throws IOException
   {
      Writer writer = new OutputStreamWriter(out, "UTF-8");
      serialize(instance, writer);
      writer.flush();
   }

   /**
    * Use writer, as JSON uses writer.
    *
    * @param instance the instance
    * @param writer the writer
    * @throws IOException for any I/O error
    */
   protected abstract void serialize(Object instance, Writer writer) throws IOException;
}

