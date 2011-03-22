package org.jboss.lhotse.common.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Simple debug tools.
 *
 * @author Marko Strukelj
 * @author Ales Justin
 */
public class DebugTools
{
   /**
    * Copy input to output.
    *
    * @param in the input
    * @param out the output
    * @throws IOException for any I/O error
    */
   public static void copyAndClose(InputStream in, OutputStream out) throws IOException
   {
      try
      {
         byte[] buf = new byte[20000];
         int rc;
         while ((rc = in.read(buf)) != -1)
         {
            out.write(buf, 0, rc);
         }
         out.flush();
         out.close();
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

   /**
    * To string input stream.
    *
    * @param in the input
    * @param ps print stream
    * @return input copy
    * @throws IOException for any I/O error
    */
   public static InputStream toString(InputStream in, PrintStream ps) throws IOException
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      copyAndClose(in, baos);
      byte[] buf = baos.toByteArray();
      ps.println("INPUT: " + new String(buf, "UTF-8"));
      return new ByteArrayInputStream(buf);
   }
}
