package org.jboss.capedwarf.common.io;

/**
 * Interface used to poll for progress info
 *
 * @author <a href="mailto:marko.strukelj@gmail.com>Marko Strukelj</a>
 */
public interface Progress {
    /**
     * Get total number of bytes to process
     *
     * @return total number of bytes
     */
    public long getTotal();

    /**
     * Get number of bytes processed
     *
     * @return number of bytes processed
     */
    public long getProcessed();
}
