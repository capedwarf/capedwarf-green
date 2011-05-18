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

package org.jboss.test.lhotse.connect.test;

import org.jboss.lhotse.common.data.UserInfo;
import org.jboss.lhotse.connect.server.ServerProxyFactory;
import org.jboss.test.lhotse.connect.support.TestProxy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Simple connect tests.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SmokeTestCase
{
   @BeforeClass
   public static void startServer()
   {
      // TODO -- start some embedded server?
   }

   @AfterClass
   public static void stopServer()
   {
      // TODO -- stop embedded server?
   }

   @Test
   public void testBasic() throws Throwable
   {
      TestProxy proxy = ServerProxyFactory.create(TestProxy.class);
      try
      {
         try
         {
            proxy.fooBar(123);
         }
         catch (Throwable ignored)
         {
            // TODo -- remove
         }
         try
         {
            proxy.infoPoke(new UserInfo("alesj", "qwert123"));
         }
         catch (Throwable ignored)
         {
            // TODo -- remove
         }
      }
      finally
      {
         ServerProxyFactory.shutdown(TestProxy.class);
      }
   }
}
