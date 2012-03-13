package org.jboss.capedwarf.common.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An InputStream with length info, and counting of bytes.
 * Bytes are read as long as delegate stream delivers them, regardless of length field.
 * It is up to the user of this class to make sure length field reflects an actual stream size.
 *
 * @author <a href="mailto:marko.strukelj@gmail.com>Marko Strukelj</a>
 */
public class FixedLengthInputStream extends FilterInputStream implements Progress {

    private long length = -1;

    private long readCount = 0;

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param in the underlying input stream, or <code>null</code> if
     *           this instance is to be created without an underlying stream.
     */
    public FixedLengthInputStream(InputStream in, long length) {
        super(in);
        this.length = length;
    }

    /**
     * Get total number of bytes to read
     *
     * @return length in bytes
     */
    public long getLength() {
        return length;
    }

    /**
     * Get number of bytes already read
     *
     * @return bytes read
     */
    public long getReadCount() {
        return readCount;
    }

    @Override
    public int read() throws IOException {
        int c = super.read();
        if (c != -1)
            readCount++;
        return c;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int rc = super.read(b, off, len);
        if (rc != -1)
            readCount += rc;
        return rc;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = super.skip(n);
        if (skipped >= 0)
            readCount += skipped;
        return skipped;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public void mark(int readlimit) {
        // not implemented
    }

    @Override
    public void reset() throws IOException {
        // not implemented
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    public long getTotal() {
        return length;
    }

    public long getProcessed() {
        return readCount;
    }
}
