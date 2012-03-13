package org.jboss.capedwarf.common.dto;

/**
 * Convert value.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ValueConverter<U, T> {
    /**
     * Convert value.
     *
     * @param value the original value
     * @return converted value
     */
    U convert(T value);
}
