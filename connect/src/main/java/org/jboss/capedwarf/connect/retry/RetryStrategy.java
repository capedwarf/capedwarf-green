package org.jboss.capedwarf.connect.retry;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface RetryStrategy {
    /**
     * Retry.
     *
     * @param context the retry context
     * @return retry return value
     * @throws Throwable throwable
     */
    Object retry(RetryContext context) throws Throwable;
}
