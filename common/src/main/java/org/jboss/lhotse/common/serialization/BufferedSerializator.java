package org.jboss.lhotse.common.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import org.jboss.lhotse.common.tools.DebugTools;

/**
 * Buffered serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class BufferedSerializator extends DelegateSerializator
{
   public BufferedSerializator(Serializator delegate)
   {
      super(delegate);
   }

   public void serialize(Object instance, OutputStream out) throws IOException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      delegate.serialize(instance, baos);
      baos.flush();
      log.log(Level.FINEST, "Content: " + baos.toString());
      out.write(baos.toByteArray());
   }

   public <T> T deserialize(InputStream stream, Class<T> clazz) throws IOException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DebugTools.copyAndClose(stream, baos);
      byte[] buf = baos.toByteArray();
      try
      {
         return delegate.deserialize(new ByteArrayInputStream(buf), clazz);
      }
      catch (IOException e)
      {
         IOException ioe = new IOException("Content: " + new String(buf));
         ioe.initCause(e);
         throw ioe;
      }
   }
}
