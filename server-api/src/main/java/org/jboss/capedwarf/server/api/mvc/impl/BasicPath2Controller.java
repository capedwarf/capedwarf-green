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

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.capedwarf.common.env.Secure;
import org.jboss.capedwarf.server.api.servlet.AbstractRequestHandler;
import org.jboss.capedwarf.server.api.servlet.RequestHandler;

/**
 * Basic path 2 controller bridge.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public abstract class BasicPath2Controller extends AbstractRequestHandler implements Path2Controller
{
   private volatile String path;
   private RequestHandler handler;
   private BeanManager manager;

   @Inject
   public void setManager(BeanManager manager)
   {
      this.manager = manager;
   }

   /**
    * Get handler class.
    *
    * @return the handler class
    */
   protected abstract Class<? extends RequestHandler> getHandlerClass();

   public String path()
   {
      if (path == null)
      {
         Class<? extends BasicPath2Controller> clazz = getClass();

         String scn = clazz.getSimpleName();
         int p = scn.indexOf(Path2Controller.class.getSimpleName());
         String name = scn.substring(0, p);

         StringBuilder builder = new StringBuilder("/");
         Secure secure = clazz.getAnnotation(Secure.class);
         if (secure != null)
            builder.append(secure.value()).append("/");

         char[] chars = name.toCharArray();
         for (int i = 0; i < chars.length; i++)
         {
            if (Character.isUpperCase(chars[i]) && i > 0)
               builder.append('-');
            builder.append(Character.toLowerCase(chars[i]));
         }
         path = builder.toString();
      }
      return path;
   }

   @SuppressWarnings({"unchecked"})
   protected void doInitialize(ServletContext context)
   {
      InjectionTarget it = manager.createInjectionTarget(manager.createAnnotatedType(getHandlerClass()));
      CreationalContext cc = manager.createCreationalContext(null);
      handler = (RequestHandler) it.produce(cc);
      it.inject(handler, cc);

      handler.initialize(context);
   }

   public void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      handler.handle(req, resp);
   }
}
