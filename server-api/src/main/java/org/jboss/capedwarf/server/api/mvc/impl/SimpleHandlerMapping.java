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

package org.jboss.capedwarf.server.api.mvc.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import org.jboss.capedwarf.server.api.mvc.HandlerMapping;
import org.jboss.capedwarf.server.api.servlet.RequestHandler;

/**
 * Multi request handler.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class SimpleHandlerMapping implements HandlerMapping
{
   private Map<String, RequestHandler> handlers;

   public void initialize(ServletContext context)
   {
   }

   public RequestHandler findHandler(HttpServletRequest req)
   {
      String path = req.getPathInfo();
      RequestHandler handler = handlers.get(path);
      if (handler != null)
         return handler;

      for (String ep : handlers.keySet())
      {
         if (path.startsWith(ep))
            return handlers.get(ep);
      }
      return null;
   }

   @Inject
   public void setControllers(Instance<Path2Controller> paths)
   {
      handlers = new HashMap<String, RequestHandler>();
      for (Path2Controller p2c : paths)
      {
         String path = p2c.path();
         handlers.put(path, p2c);
      }
   }

   public String toString()
   {
      return handlers.toString();
   }
}
