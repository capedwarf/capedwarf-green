package org.jboss.capedwarf.common.env;

/**
 * The environment utils.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class EnvironmentFactory
{
   /** The env */
   private static volatile Environment env;

   /**
    * For test purposes only!
    *
    * @param id the user id
    */
   static void setUserId(long id)
   {
      GAEEnvironment.setUserId(id);
   }

   /**
    * For test purposes only!
    *
    * @param token the user token
    */
   static void setUserToken(String token)
   {
      GAEEnvironment.setUserToken(token);
   }

   /**
    * Get environment.
    *
    * @return the environment
    */
   public static Environment getEnvironment()
   {
      Environment tmp = env;
      if (tmp == null)
      {
         try
         {
            ClassLoader cl = Environment.class.getClassLoader();
            // let's first try the client side env
            Class clazz = cl.loadClass("org.jboss.capedwarf.client.server.AndroidEnvironment");
            Environment environment = (Environment) clazz.newInstance();
            environment.touch(); // test
            env = environment;
            return env;
         }
         catch (Throwable ignored)
         {
         }

         env = new GAEEnvironment();
      }
      return env;
   }
}
