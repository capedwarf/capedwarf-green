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

package org.jboss.test.lhotse.connect.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SunHttpServerEmbedded implements HttpServerEmbedded
{
   private HttpServer server;

   public void start() throws IOException
   {
      if (server != null)
         server.stop(0);

      InetSocketAddress isa = new InetSocketAddress("localhost", 8080);
      server = HttpServer.create(isa, 0);
      server.setExecutor(Executors.newCachedThreadPool());
      server.start();
   }

   public void addContext(String name, HttpHandler handler)
   {
      if (server != null)
         server.createContext(name, new SunHttpHandler(handler));
   }

   public void removeContext(String name)
   {
      if (server != null)
         server.removeContext(name);
   }

   public void stop()
   {
      HttpServer temp = server;
      server = null;

      if (temp != null)
         temp.stop(0);
   }

   private static class SunHttpHandler implements com.sun.net.httpserver.HttpHandler
   {
      private HttpHandler handler;

      private SunHttpHandler(HttpHandler handler)
      {
         this.handler = handler;
      }

      public void handle(HttpExchange exchange) throws IOException
      {
         exchange.sendResponseHeaders(200, 0);

         OutputStream outputStream = exchange.getResponseBody();
         try
         {
            handler.handle(new SunHttpContext(exchange));
            outputStream.flush();
         }
         finally
         {
            outputStream.close();
         }
      }
   }

   private static class SunHttpContext implements HttpContext
   {
      private HttpExchange exchange;

      private SunHttpContext(HttpExchange exchange)
      {
         this.exchange = exchange;
      }

      public InputStream getInputStream() throws IOException
      {
         return exchange.getRequestBody();
      }

      public OutputStream getOutputStream() throws IOException
      {
         return exchange.getResponseBody();
      }
   }
}
