package org.jboss.capedwarf.common.tools;

import java.io.*;

/**
 * Simple debug tools.
 *
 * @author Marko Strukelj
 * @author Ales Justin
 */
public class DebugTools {
    /**
     * To string input stream.
     *
     * @param in the input
     * @param ps print stream
     * @return input copy
     * @throws IOException for any I/O error
     */
    public static InputStream toString(InputStream in, PrintStream ps) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copyAndClose(in, baos);
        byte[] buf = baos.toByteArray();
        ps.println("INPUT: " + new String(buf, "UTF-8"));
        return new ByteArrayInputStream(buf);
    }
}
