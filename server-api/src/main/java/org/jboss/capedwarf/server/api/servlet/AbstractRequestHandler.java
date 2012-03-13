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

import javax.servlet.ServletContext;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract handle request.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractRequestHandler implements RequestHandler {
    protected static final int NOT_INSTALLED = 0;
    protected static final int INSTALLING = 1;
    protected static final int INSTALLED = 2;

    private AtomicInteger state = new AtomicInteger(NOT_INSTALLED);

    protected AtomicInteger getState() {
        return state;
    }

    protected boolean next(int expect) {
        return state.compareAndSet(expect, expect + 1);
    }

    // should not override this -- use doInitialize
    public void initialize(ServletContext context) {
        if (next(NOT_INSTALLED)) {
            doInitialize(context);
            next(INSTALLING);
        }
    }

    /**
     * Do initialize.
     *
     * @param context the servlet context
     */
    protected abstract void doInitialize(ServletContext context);
}
