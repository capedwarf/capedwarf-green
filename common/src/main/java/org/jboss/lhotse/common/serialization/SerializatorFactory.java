package org.jboss.lhotse.common.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Get Serializator per data class.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SerializatorFactory
{
   private static Map<Class, Serializator> map = new WeakHashMap<Class, Serializator>();

   /**
    * Get matching serializator.
    *
    * @param clazz the class to map serializator
    * @return matching serializator
    */
   public static Serializator get(Class<?> clazz)
   {
      if (clazz == null)
         throw new IllegalArgumentException("Null class");

      Serializator serializator = map.get(clazz);
      return (serializator != null) ? serializator : JSONSerializator.OPTIONAL_GZIP;
   }

   /**
    * Map serializator.
    * If @param serializator is null, remove the mapping.
    *
    * @param clazz the class to map
    * @param serializator the serializator
    */
   public static void mapSerializator(Class<?> clazz, Serializator serializator)
   {
      if (clazz == null)
         throw new IllegalArgumentException("Null class");

      if (serializator == null)
      {
         map.remove(clazz);
      }
      else
      {
         if (serializator.isValid(clazz) == false)
            throw new IllegalArgumentException("Class " + clazz + " is not valid for " + serializator);

         map.put(clazz, serializator);
      }
   }

   /**
    * Serialize instance.
    *
    * @param instance the instance
    * @return serialized output stream bytes
    * @throws IOException for any I/O error
    */
   public static byte[] serialize(Object instance) throws IOException
   {
      if (instance == null)
         throw new IllegalArgumentException("Null instance.");

      Class<?> clazz = instance.getClass();
      Serializator serializator = get(clazz);
      return serializator.serialize(instance);
   }

   /**
    * Deserialize input stream.
    *
    * @param stream the stream
    * @param clazz the expected class
    * @return serialized output stream
    * @throws IOException for any I/O error
    */
   public static <T> T deserialize(InputStream stream, Class<T> clazz) throws IOException
   {
      if (clazz == null)
         throw new IllegalArgumentException("Null class.");

      Serializator serializator = get(clazz);
      return serializator.deserialize(stream, clazz);
   }

   static ClassLoader getClassLoader(final Class<?> clazz)
   {
      SecurityManager sm = System.getSecurityManager();
      if (sm == null)
         return clazz.getClassLoader();
      else
         return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>()
         {
            public ClassLoader run()
            {
               return clazz.getClassLoader();
            }
         });
   }
}
