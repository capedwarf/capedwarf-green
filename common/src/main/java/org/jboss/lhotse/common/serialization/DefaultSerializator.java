package org.jboss.lhotse.common.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

/**
 * Default serialization mechanism -- using JDK serialization.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DefaultSerializator extends AbstractSerializator
{
   public static Serializator INSTANCE = new DefaultSerializator();
   public static Serializator GZIPPED = new GzipSerializator(INSTANCE);

   private DefaultSerializator()
   {
   }

   public void serialize(Object instance, OutputStream out) throws IOException
   {
      ObjectOutputStream oos = new ObjectOutputStream(out);
      oos.writeObject(instance);
      oos.flush();
   }

   public <T> T deserialize(InputStream stream, final Class<T> clazz) throws IOException
   {
      final ClassLoader cl = SerializatorFactory.getClassLoader(clazz);
      ObjectInputStream ois = new ObjectInputStream(stream)
      {
         @Override
         protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
         {
            String className = desc.getName();
            return cl.loadClass(className);
         }
      };
      try
      {
         return clazz.cast(ois.readObject());
      }
      catch (ClassNotFoundException cnfe)
      {
         IOException ioe = new IOException();
         ioe.initCause(cnfe);
         throw ioe;
      }
      finally
      {
         stream.close();
      }
   }
}
