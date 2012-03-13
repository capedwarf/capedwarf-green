package org.jboss.capedwarf.connect.config;

import org.jboss.capedwarf.common.Constants;
import org.jboss.capedwarf.common.env.Environment;
import org.jboss.capedwarf.common.env.EnvironmentFactory;
import org.jboss.capedwarf.common.env.EnvironmentType;

import java.util.Formatter;

/**
 * The default server config.
 *
 * @author Ales Justin
 * @author Marko Strukelj
 */
public class DefaultConfiguration<T> extends Configuration<T> {
    /**
     * Default debug configuration.
     */
    public DefaultConfiguration() {
        this(null);
    }

    /**
     * Default appspot configuration.
     *
     * @param appspotName the appspot name
     */
    public DefaultConfiguration(String appspotName) {
        boolean debug = (appspotName == null || appspotName.length() == 0);

        setDebugMode(debug);
        setDebugLogging(debug);

        setSslPort(443);
        setPort(isDebugMode() ? 8080 : 80);

        String localhost = "localhost";

        Environment env = EnvironmentFactory.getEnvironment();
        if (env.envType() == EnvironmentType.ANDROID)
            localhost = "://10.0.2.2";

        setHostName(isDebugMode() ?
                localhost :
                new Formatter().format(Constants.HOST, appspotName).toString()
        );
    }
}
