package org.jboss.capedwarf.common.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serialization mechanism.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface Serializator
{
   /**
    * Check if the class is valid for this Serializator.
    *
    * @param clazz the class
    * @return true if valid, false otherwise
    */
   boolean isValid(Class<?> clazz);

   /**
    * Serialize instance.
    *
    * @param instance the instance to serialize
    * @param out the output stream
    * @throws IOException for any I/O error
    */
   void serialize(Object instance, OutputStream out) throws IOException;

   /**
    * Serialize instance.
    *
    * @param instance the instance to serialize
    * @return instance's serialized output stream bytes
    * @throws IOException for any I/O error
    */
   byte[] serialize(Object instance) throws IOException;

   /**
    * Deserialize inputstream into instance.
    *
    * @param stream the serialized stream
    * @param clazz the expected class
    * @return deserialized instance
    * @throws IOException for any I/O error
    */
   <T> T deserialize(InputStream stream, Class<T> clazz) throws IOException;
}
