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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.capedwarf.server.api.servlet.AbstractRequestHandler;
import org.jboss.capedwarf.server.api.servlet.RequestHandler;

/**
 * Simple action request controller.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public abstract class ActionController extends AbstractRequestHandler
{
   private ServletContext context;
   private BeanManager beanManager;

   private Map<String, RequestHandler> actions = Collections.emptyMap();
   private Map<String, Class> classes = Collections.emptyMap();

   @Inject
   public void setBeanManager(BeanManager beanManager)
   {
      this.beanManager = beanManager;
   }

   @Override
   protected void doInitialize(ServletContext context)
   {
      this.context = context;
   }

   @SuppressWarnings({"unchecked"})
   public void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      String actionName = req.getParameter("action");
      if (actionName != null)
      {
         RequestHandler action = actions.get(actionName);
         if (action == null)
         {
            Class<RequestHandler> ac = classes.get(actionName);
            if (ac != null)
            {
               if (beanManager == null)
                  throw new IllegalArgumentException("No Weld manager present");

               InjectionTarget<RequestHandler> it = beanManager.createInjectionTarget(beanManager.createAnnotatedType(ac));
               CreationalContext<RequestHandler> cc = beanManager.createCreationalContext(null);
               action = it.produce(cc);
               it.inject(action, cc);

               action.initialize(context);

               actions.put(actionName, action);
            }
         }

         if (action != null)
            action.handle(req, resp);
         else
            throw new ServletException("No such matching action: " + actionName);
      }
   }

   /**
    * Add non stateless actions.
    *
    * @param name the action name
    * @param clazz the action class
    */
   protected void addActionClass(String name, Class<? extends RequestHandler> clazz)
   {
      if (classes.isEmpty())
         classes = new HashMap<String, Class>();

      classes.put(name, clazz);
   }

   /**
    * Add stateless actions.
    *
    * @param name the action name
    * @param handler the action handler
    */
   protected void addAction(String name, RequestHandler handler)
   {
      if (actions.isEmpty())
         actions = new HashMap<String, RequestHandler>();

      actions.put(name, handler);
   }
}
