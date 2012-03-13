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

package org.jboss.capedwarf.server.api.security;

import org.jboss.capedwarf.server.api.admin.AdminManager;
import org.jboss.capedwarf.server.api.qualifiers.Current;
import org.jboss.capedwarf.server.api.users.User;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Security interceptor.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Security
@Interceptor
public class SecurityInterceptor implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient User user;
    private transient AdminManager adminManager;

    @AroundInvoke
    public Object aroundInvoke(final InvocationContext invocation) throws Exception {
        Method method = invocation.getMethod();
        Security security = method.getAnnotation(Security.class);
        if (security == null) {
            Class<?> clazz = invocation.getTarget().getClass();
            security = clazz.getAnnotation(Security.class);
        }

        if (security != null && security.value().length > 0) {
            if (user == null)
                throw new IllegalArgumentException("Null user, but required roles!");

            String email = user.getEmail();
            String[] roles = security.value();
            boolean allowed = false;
            for (String role : roles) {
                if (adminManager.isUserInRole(email, role)) {
                    allowed = true;
                    break;
                }
            }

            if (allowed == false)
                throw new IllegalArgumentException("Illegal user [" + user + "], missing proper role: " + Arrays.asList(roles));

            return invocation.proceed();
        } else {
            return invocation.proceed();
        }
    }

    @Inject
    public void setUser(@Current User user) {
        this.user = user;
    }

    @Inject
    public void setAdminManager(AdminManager adminManager) {
        this.adminManager = adminManager;
    }
}
