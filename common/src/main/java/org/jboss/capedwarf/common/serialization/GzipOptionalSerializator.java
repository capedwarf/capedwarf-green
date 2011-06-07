package org.jboss.capedwarf.common.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jboss.capedwarf.common.Constants;

/**
 * Optional GZIP serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GzipOptionalSerializator extends DelegateSerializator
{
   private static ThreadLocal<String> flag = new ThreadLocal<String>();

   public static void disableGzip()
   {
      flag.set(Constants.IGNORE_GZIP);
   }

   public static void enableGzip()
   {
      flag.remove();
   }

   public static boolean isGzipDisabled()
   {
      return (flag.get() != null);
   }

   public GzipOptionalSerializator(Serializator delegate)
   {
      super(delegate);
   }

   public void serialize(Object instance, OutputStream out) throws IOException
   {
      if (isGzipDisabled() == false)
      {
         GZIPOutputStream gzip = new GZIPOutputStream(out);
         delegate.serialize(instance, gzip);
         gzip.finish();
      }
      else
      {
         delegate.serialize(instance, out);
      }
   }

   public <T> T deserialize(InputStream stream, Class<T> clazz) throws IOException
   {
      if (isGzipDisabled() == false)
         stream = new GZIPInputStream(stream);

      return delegate.deserialize(stream, clazz);
   }
}
