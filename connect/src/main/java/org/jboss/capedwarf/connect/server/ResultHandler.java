package org.jboss.capedwarf.connect.server;

import org.jboss.capedwarf.common.env.Environment;

import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Result handler.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ResultHandler {
    Environment getEnv();
    Result wrapResult(Result result);
    Object toValue(Method method, Result result) throws Throwable;
    void packResponseError(Method method, InputStream stream, int status) throws Throwable;
}
