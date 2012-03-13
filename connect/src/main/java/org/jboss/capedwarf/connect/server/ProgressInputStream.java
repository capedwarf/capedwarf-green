package org.jboss.capedwarf.connect.server;

import org.jboss.capedwarf.common.io.Progress;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:marko.strukelj@gmail.com>Marko Strukelj</a>
 */
public class ProgressInputStream extends FilterInputStream {
    private Progress progress;

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param in the underlying input stream, or <code>null</code> if
     *           this instance is to be created without an underlying stream.
     */
    protected ProgressInputStream(InputStream in, Progress progress) {
        super(in);
        this.progress = progress;
    }

    public Progress getProgress() {
        return progress;
    }
}
