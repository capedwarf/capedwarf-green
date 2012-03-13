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

package org.jboss.capedwarf.server.jee.users;

import org.jboss.capedwarf.server.api.qualifiers.Current;
import org.jboss.capedwarf.server.api.users.User;
import org.jboss.capedwarf.server.api.users.UserHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * User provider.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class Users implements UserHandler {
    @Produces
    @Current
    public User currentUser(HttpServletRequest req) {
        Principal principal = req.getUserPrincipal();
        return (principal != null) ? new UserImpl(principal) : null;
    }

    public String loginURL(String requestURI) {
        return "restricted/login.cdi";
    }

    public String logoutURL(String requestURI) {
        return "restricted/logout.cdi";
    }

    private static class UserImpl implements User {
        private Principal principal;

        private UserImpl(Principal principal) {
            this.principal = principal;
        }

        public String getEmail() {
            return principal.getName();
        }

        @Override
        public String toString() {
            return getEmail();
        }
    }
}
