package org.jboss.capedwarf.connect.retry;

import org.apache.http.client.HttpClient;
import org.jboss.capedwarf.connect.server.Result;
import org.jboss.capedwarf.connect.server.ResultHandler;
import org.jboss.capedwarf.connect.server.ResultProducer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface RetryContext {
    Throwable cause();
    InvocationHandler invocationHandler();
    Method method();
    Object[] args();
    HttpClient client();
    ResultProducer producer();
    Result result();
    ResultHandler resultHandler();
}
