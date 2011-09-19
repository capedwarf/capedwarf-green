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

package org.jboss.capedwarf.server.api.mvc;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jboss.capedwarf.server.api.servlet.AbstractRequestHandler;
import org.jboss.capedwarf.server.api.servlet.RequestHandler;

/**
 * Multi request handler.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MultiRequestHandler extends AbstractRequestHandler
{
   private ServletContext context;
   private Iterable<HandlerMapping> mappings;

   protected void doInitialize(ServletContext context)
   {
      this.context = context;
      for (HandlerMapping hm : mappings)
         hm.initialize(context);
   }

   public void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      RequestHandler handler = findHandler(req);
      handler.initialize(context);
      handler.handle(req, resp);
   }

   /**
    * Find matching request handler.
    *
    * @param req the request
    * @return matching request handler
    * @throws ServletException if handler is not found
    */
   protected RequestHandler findHandler(HttpServletRequest req) throws ServletException
   {
      for (HandlerMapping hm : mappings)
      {
         RequestHandler handler = hm.findHandler(req);
         if (handler != null)
            return handler;
      }
      throw new ServletException("No such mapping: " + req.getRequestURL() + " - " + mappings);
   }

   @Inject
   public void setMappings(Instance<HandlerMapping> mappings)
   {
      Set<HandlerMapping> hms = new HashSet<HandlerMapping>();
      for (HandlerMapping hm : mappings)
         hms.add(hm);

      this.mappings = hms; 
   }
}
