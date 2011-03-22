package org.jboss.lhotse.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Delegate input stream.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ClosedInputStream extends DelegateInputStream
{
   private boolean closed;

   public ClosedInputStream(InputStream delegate)
   {
      super(delegate);
   }

   public void close() throws IOException
   {
      if (closed == false)
      {
         closed = true;
         super.close();
      }
   }
}