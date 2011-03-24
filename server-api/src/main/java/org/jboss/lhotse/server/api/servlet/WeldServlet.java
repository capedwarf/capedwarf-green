/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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

package org.jboss.lhotse.server.api.servlet;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Writer;

/**
 * Wrap plain request into Weld aware context.
 * This is a workaround until GAE support Jetty extensions.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class WeldServlet extends HttpServlet implements Filter
{
   /** The const */
   private static String REQUEST_HANDLER = "request-handler";
   /** The request handler */
   private RequestHandler handler;

   @Override
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);

      ServletContext context = config.getServletContext();
      String wrapperClass = config.getInitParameter(REQUEST_HANDLER);
      init(context, wrapperClass);
   }

   @SuppressWarnings("unchecked")
   private void init(ServletContext context, String wrapperClass) throws ServletException
   {
      if (wrapperClass == null)
         throw new IllegalArgumentException("Missing handler class parameter");

      BeanManager manager = (BeanManager) context.getAttribute(BeanManager.class.getName());
      if (manager == null)
         throw new IllegalArgumentException("No Weld manager present");

      try
      {
         ClassLoader cl = RequestHandler.class.getClassLoader();
         Class<?> tmp = cl.loadClass(wrapperClass);
         if (RequestHandler.class.isAssignableFrom(tmp) == false)
            throw new ServletException("Illegal handler class, wrong type: " + tmp);

         Class<RequestHandler> clazz = (Class<RequestHandler>) tmp;

         InjectionTarget<RequestHandler> it = manager.createInjectionTarget(manager.createAnnotatedType(clazz));
         CreationalContext<RequestHandler> cc = manager.createCreationalContext(null);
         handler = it.produce(cc);
         it.inject(handler, cc);

         handler.initialize(context);
      }
      catch (ServletException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new ServletException(e);
      }
   }

   public void init(FilterConfig config) throws ServletException
   {
      ServletContext context = config.getServletContext();
      String wrapperClass = config.getInitParameter(REQUEST_HANDLER);
      init(context, wrapperClass);
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      handle(req, resp);
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      handle(req, resp);
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
   {
      if (request instanceof HttpServletRequest && response instanceof HttpServletResponse)
      {
         if (handler instanceof FilterHandler)
         {
            FilterHandler fh = (FilterHandler) handler;
            if (fh.accepts((HttpServletRequest) request, (HttpServletResponse) response) == false)
               return; // don't go down the chain
         }
         else
         {
            handle((HttpServletRequest) request, (HttpServletResponse) response);
         }
         chain.doFilter(request, response);
      }
      else
      {
         Writer writer = response.getWriter();
         writer.write("ERROR -- can only handle Http requests / response.");
         writer.flush();
      }
   }

   protected final void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      handler.handle(req, resp);
   }
}
