package org.jboss.capedwarf.common.serialization;

import org.jboss.capedwarf.common.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Optional GZIP serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GzipOptionalSerializator extends DelegateSerializator {
    private static ThreadLocal<String> flag = new ThreadLocal<String>();

    public static void disableGzip() {
        flag.remove();
    }

    public static void enableGzip() {
        flag.set(Constants.IGNORE_GZIP);
    }

    public static boolean isGzipEnabled() {
        return (flag.get() != null);
    }

    /**
     * Wrap stream into gzip stream if gzip is enabled
     * and stream is not already gziped.
     *
     * @param stream the stream to check
     * @return gziped or same stream
     * @throws IOException for any I/O error
     */
    public static InputStream wrap(InputStream stream) throws IOException {
        return isGzipEnabled() == false || (stream instanceof GZIPInputStream) ?
                stream :
                new GZIPInputStream(stream);
    }

    /**
     * Wrap stream into gzip stream if gzip is enabled
     * and stream is not already gziped.
     *
     * @param stream the stream to check
     * @return gziped or same stream
     * @throws IOException for any I/O error
     */
    public static OutputStream wrap(OutputStream stream) throws IOException {
        return isGzipEnabled() == false || (stream instanceof GZIPOutputStream) ?
                stream :
                new GZIPOutputStream(stream);
    }

    public GzipOptionalSerializator(Serializator delegate) {
        super(delegate);
    }

    public void serialize(Object instance, OutputStream out) throws IOException {
        if (isGzipEnabled()) {
            GZIPOutputStream gzip = new GZIPOutputStream(out);
            delegate.serialize(instance, gzip);
            gzip.finish();
        } else {
            delegate.serialize(instance, out);
        }
    }

    public <T> T deserialize(InputStream stream, Class<T> clazz) throws IOException {
        if (isGzipEnabled())
            stream = new GZIPInputStream(stream);

        return delegate.deserialize(stream, clazz);
    }
}
