package org.jboss.capedwarf.common.serialization;

import org.jboss.capedwarf.common.tools.IOUtils;

import java.io.*;
import java.util.logging.Level;

/**
 * Buffered serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class BufferedSerializator extends DelegateSerializator {
    public BufferedSerializator(Serializator delegate) {
        super(delegate);
    }

    public void serialize(Object instance, OutputStream out) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        delegate.serialize(instance, baos);
        baos.flush();
        log.log(Level.FINEST, "Content: " + baos.toString());
        out.write(baos.toByteArray());
    }

    public <T> T deserialize(InputStream stream, Class<T> clazz) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copyAndClose(stream, baos);
        byte[] buf = baos.toByteArray();
        try {
            return delegate.deserialize(new ByteArrayInputStream(buf), clazz);
        } catch (IOException e) {
            IOException ioe = new IOException("Content: " + new String(buf));
            ioe.initCause(e);
            throw ioe;
        }
    }
}
