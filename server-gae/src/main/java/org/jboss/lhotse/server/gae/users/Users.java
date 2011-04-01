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

package org.jboss.lhotse.server.gae.users;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.jboss.lhotse.server.api.quilifiers.Current;
import org.jboss.lhotse.server.api.users.User;
import org.jboss.lhotse.server.api.users.UserHandler;

/**
 * User provider.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class Users implements UserHandler
{
   private UserService userService = UserServiceFactory.getUserService();

   @Produces
   @Current
   public User currentUser()
   {
      com.google.appengine.api.users.User user = userService.getCurrentUser();
      return (user != null) ? new UserImpl(user) : null;
   }

   public String loginURL(String requestURI)
   {
      return userService.createLoginURL(requestURI);
   }

   private static class UserImpl implements User
   {
      com.google.appengine.api.users.User user;

      private UserImpl(com.google.appengine.api.users.User user)
      {
         this.user = user;
      }

      public String getEmail()
      {
         return user.getEmail();
      }
   }
}
