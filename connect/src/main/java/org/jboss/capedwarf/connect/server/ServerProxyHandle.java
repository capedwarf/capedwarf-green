package org.jboss.capedwarf.connect.server;

/**
 * ServerProxy handle.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
interface ServerProxyHandle {
    /**
     * Is streaming allowed.
     *
     * @param allowsStreaming the streaming flag
     */
    void setAllowsStreaming(boolean allowsStreaming);

    /**
     * Shutdown server proxy.
     */
    void shutdown();
}
