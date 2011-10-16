/*
* JBoss, Home of Professional Open Source
* Copyright $today.year Red Hat Inc. and/or its affiliates and other
* contributors as indicated by the @author tags. All rights reserved.
* See the copyright.txt in the distribution for a full listing of
* individual contributors.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/

package org.jboss.test.capedwarf.connect.test;

import org.jboss.capedwarf.connect.config.Configuration;
import org.jboss.capedwarf.connect.config.DefaultConfiguration;
import org.junit.Assert;
import org.junit.Test;

/**
 * Simple config tests.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ConfigTestCase extends AbstractConnectTest
{
   @Test
   public void testBasic() throws Throwable
   {
      Configuration config = new TestConfiguration("nba.com", "cd", "my", "restricted", 8080, 4443);
      Assert.assertEquals("http://nba.com:8080/cd/my/", config.getEndpoint(false));
      Assert.assertEquals("https://nba.com:4443/cd/my/restricted/", config.getEndpoint(true));

      config = new DefaultConfiguration("b-s");
      config.setHostName("c-b.org");
      config.setAppContext("b-s");
      config.setStrictSSL(true);
      config.setPort(4443);
      config.setSslPort(4443);
      Assert.assertEquals("https://c-b.org:4443/b-s/client/", config.getEndpoint(false));
      Assert.assertEquals("https://c-b.org:4443/b-s/client/secure/", config.getEndpoint(true));

      config = new DefaultConfiguration("b-s");
      config.setHostName("c-b.org/");
      config.setAppContext("b-s");
      config.setStrictSSL(true);
      config.setPort(4443);
      config.setSslPort(4443);
      config.setStrictPort(false);
      Assert.assertEquals("https://c-b.org/b-s/client/", config.getEndpoint(false));
      Assert.assertEquals("https://c-b.org/b-s/client/secure/", config.getEndpoint(true));
   }

   private static class TestConfiguration extends Configuration
   {
      private TestConfiguration(String hostName, String app, String client, String secure, int port, int sslPort)
      {
         setHostName(hostName);
         setAppContext(app);
         setClientContext(client);
         setSecureContext(secure);
         setPort(port);
         setSslPort(sslPort);
      }
   }
}
