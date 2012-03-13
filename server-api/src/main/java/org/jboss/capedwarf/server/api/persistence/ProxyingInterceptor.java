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

package org.jboss.capedwarf.server.api.persistence;

import org.jboss.capedwarf.jpa.ProxyingEnum;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Proxying interceptor.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Proxying(ProxyingEnum.ENABLE)
@Interceptor
public class ProxyingInterceptor implements Serializable {
    private static final long serialVersionUID = 1L;

    @AroundInvoke
    public Object aroundInvoke(final InvocationContext invocation) throws Exception {
        Method method = invocation.getMethod();
        Proxying proxying = method.getAnnotation(Proxying.class);
        if (proxying == null) {
            Class<?> clazz = invocation.getTarget().getClass();
            proxying = clazz.getAnnotation(Proxying.class);
        }

        if (proxying != null) {
            ProxyingEnum pe = proxying.value();
            pe.begin();
            try {
                return invocation.proceed();
            } finally {
                pe.end();
            }
        } else {
            return invocation.proceed();
        }
    }
}
