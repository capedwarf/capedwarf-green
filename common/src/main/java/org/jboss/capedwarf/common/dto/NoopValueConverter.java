package org.jboss.capedwarf.common.dto;

/**
 * Noop value converter.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class NoopValueConverter<R> implements ValueConverter<R, R> {
    public static final NoopValueConverter<Object> INSTANCE = new NoopValueConverter<Object>();

    public R convert(R value) {
        return value;
    }
}
