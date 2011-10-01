package org.jboss.capedwarf.common.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Simple I/O utils.
 *
 * @author Marko Strukelj
 * @author Ales Justin
 */
public class IOUtils
{
   /**
    * Copy input to output.
    *
    * @param in the input
    * @param out the output
    * @throws java.io.IOException for any I/O error
    */
   public static void copyAndClose(InputStream in, OutputStream out) throws IOException
   {
      copyAndClose(in, out, true);
   }

   /**
    * Copy input to output.
    *
    * @param in the input
    * @param out the output
    * @param flushAndCloseOut do we flush and close out stream
    * @throws java.io.IOException for any I/O error
    */
   public static void copyAndClose(InputStream in, OutputStream out, boolean flushAndCloseOut) throws IOException
   {
      try
      {
         byte[] buf = new byte[20000];
         int rc;
         while ((rc = in.read(buf)) != -1)
         {
            out.write(buf, 0, rc);
         }
         if (flushAndCloseOut)
         {
            out.flush();
            out.close();
         }
      }
      finally
      {
         try
         {
            in.close();
         }
         catch (Exception ignored)
         {
         }
      }
   }
}
