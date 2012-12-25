package org.jboss.capedwarf.connect.retry;

import org.jboss.capedwarf.common.Constants;
import org.jboss.capedwarf.connect.config.Configuration;
import org.jboss.capedwarf.connect.server.Result;
import org.jboss.capedwarf.connect.server.ResultHandler;

import java.util.logging.Level;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GaeRetryStrategy implements RetryStrategy {
    public Object retry(RetryContext context) throws Throwable {
        Configuration config = Configuration.getInstance();
        Result result = context.result();
        // Lets retry if we're over GAE limit
        if (config.isRepeatRequest() && result.getExecutionTime() > 29 * 1000) {
            ResultHandler resultHandler = context.resultHandler();
            resultHandler.getEnv().log(Constants.TAG_CONNECTION, Level.CONFIG, "Retrying, hit GAE limit: " + (result.getExecutionTime() / 1000), null);
            result = resultHandler.wrapResult(context.producer().run());
            if (result.getStatus() != 200) {
                resultHandler.packResponseError(context.method(), result.getStream(), result.getStatus());
            }
            return resultHandler.toValue(context.method(), result);
        } else {
            throw context.cause();
        }
    }
}
