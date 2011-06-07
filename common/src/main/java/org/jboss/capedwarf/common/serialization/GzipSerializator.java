package org.jboss.capedwarf.common.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP based serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GzipSerializator extends DelegateSerializator
{
   public GzipSerializator(Serializator delegate)
   {
      super(delegate);
   }

   public void serialize(Object instance, OutputStream out) throws IOException
   {
      GZIPOutputStream gzip = new GZIPOutputStream(out);
      delegate.serialize(instance, gzip);
      gzip.finish();
   }

   public <T> T deserialize(InputStream stream, Class<T> clazz) throws IOException
   {
      InputStream gzip = new GZIPInputStream(stream);
      return delegate.deserialize(gzip, clazz);
   }
}
