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

import org.jboss.capedwarf.common.data.Status;
import org.jboss.capedwarf.common.data.StatusInfo;
import org.jboss.capedwarf.common.data.UserInfo;
import org.jboss.capedwarf.common.serialization.JSONSerializator;
import org.jboss.capedwarf.common.serialization.Serializator;
import org.jboss.capedwarf.common.tools.IOUtils;
import org.jboss.capedwarf.connect.io.GzipContentProducer;
import org.jboss.capedwarf.connect.server.ServerProxyFactory;
import org.jboss.test.capedwarf.connect.support.HttpContext;
import org.jboss.test.capedwarf.connect.support.HttpHandler;
import org.jboss.test.capedwarf.connect.support.TestProxy;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Simple connect tests.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SmokeTestCase extends AbstractConnectTest {
    @Test
    public void testBasic() throws Throwable {
        HttpHandler foobar = new HttpHandler() {
            public void handle(HttpContext context) throws IOException {
                OutputStream outputStream = context.getOutputStream();
                outputStream.write("321".getBytes());
            }
        };
        HttpHandler primitive = new HttpHandler() {
            public void handle(HttpContext context) throws IOException {
                OutputStream outputStream = context.getOutputStream();
                outputStream.write("321".getBytes());
            }
        };
        HttpHandler info = new HttpHandler() {
            public void handle(HttpContext context) throws IOException {
                OutputStream outputStream = context.getOutputStream();
                Serializator gs = JSONSerializator.OPTIONAL_GZIP_BUFFERED;
                gs.serialize(new StatusInfo(Status.OK), outputStream);
            }
        };
        HttpHandler content = new HttpHandler() {
            public void handle(HttpContext context) throws IOException {
                InputStream inputStream = context.getInputStream();
                GZIPInputStream gzip = new GZIPInputStream(inputStream);
                byte[] buf = new byte[1000];
                gzip.read(buf);
                String input = new String(buf);
                System.out.println("input = " + input);
                GZIPOutputStream outputStream = new GZIPOutputStream(context.getOutputStream());
                outputStream.write("OK".getBytes());
                outputStream.finish();
            }
        };

        getServer().addContext("/client/foo", foobar);
        getServer().addContext("/client/primitive", primitive);
        getServer().addContext("/client/info", info);
        getServer().addContext("/client/content", content);
        try {
            TestProxy proxy = ServerProxyFactory.create(TestProxy.class);
            try {
                String s = proxy.fooBar(123);
                Assert.assertEquals("321", s);

                long x = proxy.primitive(123);
                Assert.assertEquals(321L, x);

                StatusInfo status = proxy.infoPoke(new UserInfo("alesj", "qwert123"));
                Assert.assertEquals(Status.OK, status.getStatus());

                GzipContentProducer cp = new GzipContentProducer() {
                    protected void doWriteTo(OutputStream outstream) throws IOException {
                        outstream.write("POKE?".getBytes());
                    }
                };
                InputStream is = proxy.contentDirect(123, cp, 321);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOUtils.copyAndClose(is, baos);
                String ok = new String(baos.toByteArray());
                Assert.assertEquals("OK", ok);
            } finally {
                ServerProxyFactory.shutdown(TestProxy.class);
            }
        } finally {
            getServer().removeContext("/client/foo");
            getServer().removeContext("/client/info");
            getServer().removeContext("/client/content");
        }
    }
}
