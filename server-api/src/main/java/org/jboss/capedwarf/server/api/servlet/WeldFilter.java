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

package org.jboss.capedwarf.server.api.servlet;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Exposes request and response as beans.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class WeldFilter implements Filter {
    /**
     * The bean manager
     */
    private BeanManager manager;

    public void init(FilterConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        manager = BeanManagerUtils.lookup(context);
    }

    protected <T> T getBean(Class<T> beanType) {
        Bean<?> bean = manager.resolve(manager.getBeans(beanType));
        Object result = manager.getReference(bean, beanType, manager.createCreationalContext(bean));
        return beanType.cast(result);
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            getBean(HttpServletRequestManager.class).setRequest((HttpServletRequest) request);
            getBean(HttpServletResponseManager.class).setResponse((HttpServletResponse) response);
        }

        chain.doFilter(request, response);
    }

    public void destroy() {
        manager = null;
    }
}
