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

import java.io.IOException;
import java.io.OutputStream;

import org.jboss.capedwarf.common.data.Status;
import org.jboss.capedwarf.common.data.StatusInfo;
import org.jboss.capedwarf.common.data.UserInfo;
import org.jboss.capedwarf.common.serialization.JSONSerializator;
import org.jboss.capedwarf.common.serialization.Serializator;
import org.jboss.capedwarf.connect.server.ServerProxyFactory;
import org.jboss.test.capedwarf.connect.support.HttpContext;
import org.jboss.test.capedwarf.connect.support.HttpHandler;
import org.jboss.test.capedwarf.connect.support.TestProxy;
import org.junit.Assert;
import org.junit.Test;

/**
 * Simple connect tests.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SmokeTestCase extends AbstractConnectTest
{
   @Test
   public void testBasic() throws Throwable
   {
      HttpHandler foobar = new HttpHandler()
      {
         public void handle(HttpContext context) throws IOException
         {
            OutputStream outputStream = context.getOutputStream();
            outputStream.write("321".getBytes());
         }
      };
      HttpHandler info = new HttpHandler()
      {
         public void handle(HttpContext context) throws IOException
         {
            OutputStream outputStream = context.getOutputStream();
            Serializator gs = JSONSerializator.OPTIONAL_GZIP_BUFFERED;
            gs.serialize(new StatusInfo(Status.OK), outputStream);
         }
      };

      getServer().addContext("/client/foo", foobar);
      getServer().addContext("/client/info", info);
      try
      {
         TestProxy proxy = ServerProxyFactory.create(TestProxy.class);
         try
         {
            String s = proxy.fooBar(123);
            Assert.assertEquals("321", s);

            StatusInfo status = proxy.infoPoke(new UserInfo("alesj", "qwert123"));
            Assert.assertEquals(Status.OK, status.getStatus());
         }
         finally
         {
            ServerProxyFactory.shutdown(TestProxy.class);
         }
      }
      finally
      {
         getServer().removeContext("/client/foo");
         getServer().removeContext("/client/info");
      }
   }
}
