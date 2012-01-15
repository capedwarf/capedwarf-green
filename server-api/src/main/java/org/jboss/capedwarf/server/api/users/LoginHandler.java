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

package org.jboss.capedwarf.server.api.users;

import org.jboss.capedwarf.server.api.qualifiers.Current;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Login / logout bean.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Named("LoginHandler")
public class LoginHandler
{
   private User user;
   private UserHandler userHandler;
   private HttpServletRequest request;
   private HttpServletResponse response;

   /**
    * Login.
    *
    * @throws IOException for any error
    */
   public void login() throws IOException
   {
      if (user == null)
      {
         String requestURI = request.getRequestURI();
         String loginURL = userHandler.loginURL(requestURI);
         response.sendRedirect(loginURL);
      }
   }

   /**
    * Logout.
    *
    * @throws IOException for any error
    */
   public void logout() throws IOException
   {
      if (user != null)
      {
         String requestURI = request.getRequestURI();
         String logoutURL = userHandler.logoutURL(requestURI);
         response.sendRedirect(logoutURL);
      }
   }

   @Inject
   public void setUser(@Current User user)
   {
      this.user = user;
   }

   @Inject
   public void setUserHandler(UserHandler userHandler)
   {
      this.userHandler = userHandler;
   }

   @Inject
   public void setRequest(HttpServletRequest request)
   {
      this.request = request;
   }

   @Inject
   public void setResponse(HttpServletResponse response)
   {
      this.response = response;
   }
}