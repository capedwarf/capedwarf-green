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

package org.jboss.capedwarf.server.api.ui;

import org.jboss.capedwarf.server.api.admin.AdminManager;
import org.jboss.capedwarf.server.api.qualifiers.Current;
import org.jboss.capedwarf.server.api.users.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Custom login.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Named("login")
@RequestScoped
public class Login implements Serializable
{
   private static final long serialVersionUID = 1l;

   /** The admin manager */
   private AdminManager adminManager;

   /** The current user */
   private User user;

   /**
    * Is the user logged in.
    *
    * @param role the role
    * @return true if logged in, false otherwise
    */
   protected boolean isLoggedIn(String role)
   {
      if (user != null)
      {
         String email = user.getEmail();
         return adminManager.isUserInRole(email, role);
      }
      return false;
   }

   /**
    * Is the user logged in.
    *
    * @return true if user is logged in, false otherwise
    */
   public boolean isLoggedIn()
   {
      return (user != null);
   }

   /**
    * Is the admin logged in.
    *
    * @return true if admin is logged in, false otherwise
    */
   public boolean isAdmin()
   {
      return isLoggedIn("admin");
   }

   /**
    * Is the editor logged in.
    *
    * @return true if admin is logged in, false otherwise
    */
   public boolean isEditor()
   {
      return isLoggedIn("editor") || isAdmin();
   }

   /**
    * Get user.
    *
    * @return the current user
    */
   public User getUser()
   {
      return user;
   }

   @Inject
   public void setAdminManager(AdminManager adminManager)
   {
      this.adminManager = adminManager;
   }

   /**
    * Set user.
    *
    * @param user the user
    */
   @Inject
   public void setUser(@Current User user)
   {
      this.user = user;
   }
}
