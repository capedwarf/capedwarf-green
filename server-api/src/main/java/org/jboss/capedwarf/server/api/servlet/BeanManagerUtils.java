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

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;
import java.util.ServiceLoader;

/**
 * BM lookup impl.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class BeanManagerUtils {
    /**
     * The impl detail
     */
    private static final String BM_KEY = "org.jboss.weld.environment.servlet" + "." + BeanManager.class.getName();

    /**
     * Get bean manager.
     *
     * @param context the servlet context
     * @return bean manager
     */
    public static BeanManager lookup(ServletContext context) {
        // check cache
        BeanManager manager = (BeanManager) context.getAttribute(BeanManagerUtils.class.getName());
        if (manager != null)
            return manager;

        manager = doLookup(context);

        // cache it
        context.setAttribute(BeanManagerUtils.class.getName(), manager);

        return manager;
    }

    protected static BeanManager doLookup(ServletContext context) {
        ServiceLoader<BeanManagerLookup> bmls = ServiceLoader.load(BeanManagerLookup.class, BeanManagerUtils.class.getClassLoader());
        for (BeanManagerLookup bml : bmls) {
            BeanManager manager = bml.lookup(context);
            if (manager != null)
                return manager;
        }

        // Use Weld default
        BeanManager manager = (BeanManager) context.getAttribute(BM_KEY);
        if (manager == null)
            throw new IllegalArgumentException("No Weld manager present");

        return manager;
    }
}
