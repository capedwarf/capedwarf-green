package org.jboss.capedwarf.common.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Delegate input stream.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class DelegateInputStream extends InputStream {
    private InputStream delegate;

    protected DelegateInputStream(InputStream delegate) {
        if (delegate == null)
            throw new IllegalArgumentException("Null delegate");
        this.delegate = delegate;
    }

    public int read(byte[] b) throws IOException {
        return delegate.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return delegate.read(b, off, len);
    }

    public long skip(long n) throws IOException {
        return delegate.skip(n);
    }

    public int available() throws IOException {
        return delegate.available();
    }

    public void close() throws IOException {
        delegate.close();
    }

    public void mark(int readlimit) {
        delegate.mark(readlimit);
    }

    public void reset() throws IOException {
        delegate.reset();
    }

    public boolean markSupported() {
        return delegate.markSupported();
    }

    public int read() throws IOException {
        return delegate.read();
    }
}