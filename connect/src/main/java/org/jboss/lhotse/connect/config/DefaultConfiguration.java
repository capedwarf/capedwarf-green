package org.jboss.lhotse.connect.config;

import org.jboss.lhotse.common.Constants;
import org.jboss.lhotse.common.env.Environment;
import org.jboss.lhotse.common.env.EnvironmentFactory;
import org.jboss.lhotse.common.env.EnvironmentType;

/**
 * The default server config.
 *
 * @author Ales Justin
 * @author Marko Strukelj
 */
public class DefaultConfiguration<T> extends Configuration<T>
{
   public DefaultConfiguration()
   {
      this(false);
   }

   public DefaultConfiguration(boolean debug)
   {
      setDebugMode(debug);
      setDebugLogging(debug);
      
      setSslPort(443);
      setPort(isDebugMode() ? 8080 : 80);

      String localhost = "localhost";
      int port = getPort();
      if (port != 80)
         localhost += (":" + port);

      Environment env = EnvironmentFactory.getEnvironment();
      if (env.envType() == EnvironmentType.ANDROID)
         localhost = "://10.0.2.2:8080";
      
      setHostName(isDebugMode() ? localhost : Constants.HOST); // TODO
   }
}
