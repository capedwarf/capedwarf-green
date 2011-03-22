package org.jboss.lhotse.connect.server;

import java.lang.reflect.InvocationHandler;

/**
 * ServerProxy invocation handler.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ServerProxyInvocationHandler extends InvocationHandler
{
   /**
    * Shutdown server proxy handler.
    */
   void shutdown();
}
