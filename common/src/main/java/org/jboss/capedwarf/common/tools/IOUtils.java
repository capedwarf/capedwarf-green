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
public class IOUtils {
    /**
     * Copy input to output.
     *
     * @param in  the input
     * @param out the output
     * @throws java.io.IOException for any I/O error
     */
    public static void copyAndClose(InputStream in, OutputStream out) throws IOException {
        copyAndClose(in, out, true);
    }

    /**
     * Copy input to output.
     *
     * @param in               the input
     * @param out              the output
     * @param flushAndCloseOut do we flush and close out stream
     * @throws java.io.IOException for any I/O error
     */
    public static void copyAndClose(InputStream in, OutputStream out, boolean flushAndCloseOut) throws IOException {
        try {
            byte[] buf = new byte[20000];
            int rc;
            while ((rc = in.read(buf)) != -1) {
                out.write(buf, 0, rc);
            }
            if (flushAndCloseOut) {
                out.flush();
                out.close();
            }
        } finally {
            try {
                in.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * This method reads the contents of the file into a preallocated buffer.
     * If the content doesn't fit exactly, throw IllegalArgumentException.
     *
     * @param is     InputStream to read from
     * @param buffer Buffer to fill with content
     * @throws IOException for any I/O error
     */
    public static void readToBuffer(InputStream is, byte[] buffer) throws IOException {
        readToBuffer(is, buffer, 0, buffer.length);
    }

    /**
     * This method reads the contents of the file into a preallocated buffer.
     * If the content length doesn't match 'len' size exactly, an IllegalArgumentException is thrown.
     *
     * @param is     InputStream to read from
     * @param buffer Buffer to fill with content
     * @param offs   the offset
     * @param len    the length
     * @throws IOException for any I/O error
     */
    public static void readToBuffer(InputStream is, byte[] buffer, int offs, int len) throws IOException {
        try {
            int rc = is.read(buffer, offs, len);
            while (rc != -1) {
                offs += rc;
                len -= rc;
                rc = is.read(buffer, offs, len);
            }
            if (len > 0)
                throw new IllegalArgumentException("Content is shorter than expected");
            if (is.read() != -1)
                throw new IllegalArgumentException("Content is longer than expected");
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }
}
