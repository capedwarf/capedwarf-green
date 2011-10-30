package org.jboss.capedwarf.common.env;

import java.util.ServiceLoader;

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
      return getEnvironment(Environment.class.getClassLoader());
   }

   /**
    * Get environment.
    *
    * @param cl the classloader to do the lookup
    * @return the environment
    */
   public static Environment getEnvironment(ClassLoader cl)
   {
      if (env == null)
      {
         if (cl == null)
            cl = Environment.class.getClassLoader();

         ServiceLoader<Environment> envs = ServiceLoader.load(Environment.class, cl);
         for (Environment e : envs)
         {
            try
            {
               e.touch(); // test
               env = e;
               return env;
            }
            catch (Throwable ignored)
            {
            }
         }
         // fall back to gae / simple env
         env = new GAEEnvironment();
      }
      return env;
   }
}
