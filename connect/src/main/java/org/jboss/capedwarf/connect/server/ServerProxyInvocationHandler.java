package org.jboss.capedwarf.connect.server;

import java.lang.reflect.InvocationHandler;

/**
 * ServerProxy invocation handler.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ServerProxyInvocationHandler extends InvocationHandler
{
   /**
    * Is streaming allowed.
    *
    * @param allowsStreaming the streaming flag
    */
   void setAllowsStreaming(boolean allowsStreaming);

   /**
    * Shutdown server proxy handler.
    */
   void shutdown();
}
