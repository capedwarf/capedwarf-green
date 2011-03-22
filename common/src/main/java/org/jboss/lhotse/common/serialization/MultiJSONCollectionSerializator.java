package org.jboss.lhotse.common.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * JSON collection based serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MultiJSONCollectionSerializator extends AbstractJSONSerializator
{
   private JSONAwareInstanceProvider instanceProvider;

   public MultiJSONCollectionSerializator(JSONAwareInstanceProvider instanceProvider)
   {
      if (instanceProvider == null)
         throw new IllegalArgumentException("Null instance provider");
      this.instanceProvider = instanceProvider;
   }

   public boolean isValid(Class<?> clazz)
   {
      return Collection.class.isAssignableFrom(clazz);
   }

   @SuppressWarnings({"unchecked"})
   public void serialize(Object instance, Writer writer) throws IOException
   {
      if (instance instanceof Collection == false)
         throw new IllegalArgumentException("Not a collection: " + instance);

      Collection<JSONAware> collection = (Collection<JSONAware>) instance;
      JSONArray jarray = new JSONArray();
      try
      {
         for (JSONAware elt : collection)
         {
            JSONObject jsonObject = createObject();
            elt.writeJSONObject(jsonObject);
            jarray.put(jsonObject);
         }
         writeArray(jarray, writer);
      }
      catch (JSONException e)
      {
         IOException ioe = new IOException();
         ioe.initCause(e);
         throw ioe;
      }
   }

   @SuppressWarnings({"unchecked"})
   public <T> T deserialize(InputStream stream, Class<T> clazz) throws IOException
   {
      if (isValid(clazz) == false)
         throw new IllegalArgumentException("Not a JSONAware class: " + clazz);

      try
      {
         JSONTokener tokener = createTokener(stream);
         JSONArray value = new JSONArray(tokener);
         T instance = clazz.newInstance();
         Collection<JSONAware> collection = (Collection<JSONAware>) instance;
         for (int i = 0; i < value.length(); i++)
         {
            JSONAware ja = instanceProvider.createInstance(i);
            ja.readJSONObject(value.getJSONObject(i));
            collection.add(ja);
         }
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
