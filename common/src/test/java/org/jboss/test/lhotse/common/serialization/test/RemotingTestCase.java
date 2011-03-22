package org.jboss.test.lhotse.common.serialization.test;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

/**
 * Test remoting.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings({"unchecked"})
public class RemotingTestCase
{
   @Test
   public void testFakeRemoting() throws Exception
   {
      File file = new File("/Users/alesj/java_lib/android-sdk-mac_86/platforms/android-1.5/android.jar");
      if (file.exists() == false)
         return;

      ClassLoader cl = new URLClassLoader(new URL[]{file.toURI().toURL()});
      Class<?> jsonClass = cl.loadClass(JSONObject.class.getName());
      Object jsonInstance = jsonClass.newInstance();
      Method put = jsonClass.getMethod("put", String.class, Object.class);
      put.invoke(jsonInstance, "key", "Dummy. ;-)");
      String toString = jsonInstance.toString();
      JSONObject jo = new JSONObject(new JSONTokener(new StringReader(toString)));
      System.out.println("jo.getString(\"key\") = " + jo.getString("key"));
   }
}
