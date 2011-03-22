package org.jboss.lhotse.common.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * JSON based serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JSONSerializator extends AbstractJSONSerializator
{
   public static final Serializator INSTANCE = new JSONSerializator();
   public static final Serializator BUFFERED = new BufferedSerializator(INSTANCE);
   public static final Serializator GZIPPED = new GzipSerializator(INSTANCE);
   public static final Serializator GZIPPED_BUFFERED = new GzipSerializator(BUFFERED);
   public static final Serializator OPTIONAL_GZIP = new GzipOptionalSerializator(INSTANCE);
   public static final Serializator OPTIONAL_GZIP_BUFFERED = new GzipOptionalSerializator(BUFFERED);

   private JSONSerializator()
   {
   }

   public boolean isValid(Class<?> clazz)
   {
      return JSONAware.class.isAssignableFrom(clazz);
   }

   protected void serialize(Object instance, Writer writer) throws IOException
   {
      if (instance == null || isValid(instance.getClass()) == false)
         throw new IllegalArgumentException("Not a JSONAware instance: " + instance);

      JSONObject jobj = createObject();
      try
      {
         ((JSONAware)instance).writeJSONObject(jobj);
         writeObject(jobj, writer);
      }
      catch (JSONException e)
      {
         IOException ioe = new IOException();
         ioe.initCause(e);
         throw ioe;
      }
   }

   public <T> T deserialize(InputStream stream, Class<T> clazz) throws IOException
   {
      if (isValid(clazz) == false)
         throw new IllegalArgumentException("Not a JSONAware class: " + clazz);

      try
      {
         JSONTokener tokener = createTokener(stream);
         JSONObject value = new JSONObject(tokener);
         T instance = clazz.newInstance();
         ((JSONAware)instance).readJSONObject(value);
         return instance;
      }
      catch (Exception e)
      {
         IOException ioe = new IOException();
         ioe.initCause(e);
         throw ioe;
      }
      finally
      {
         stream.close();
      }
   }
}
