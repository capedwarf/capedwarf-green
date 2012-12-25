package org.jboss.capedwarf.connect.server;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ResultProducer {
    Result run() throws Throwable;
}
