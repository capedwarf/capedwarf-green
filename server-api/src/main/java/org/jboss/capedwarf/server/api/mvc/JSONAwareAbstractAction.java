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

import org.jboss.capedwarf.common.serialization.JSONAware;
import org.jboss.capedwarf.common.serialization.JSONSerializator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * JSON aware abstract action.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class JSONAwareAbstractAction<T extends JSONAware> extends ResultAbstractAction<T> {
    protected static <I> I deserialize(HttpServletRequest req, Class<I> clazz) throws IOException {
        return JSONSerializator.OPTIONAL_GZIP_BUFFERED.deserialize(req.getInputStream(), clazz);
    }

    protected void doWriteResult(HttpServletResponse resp, T result) throws IOException {
        prepareResponse(resp);
        JSONSerializator.OPTIONAL_GZIP.serialize(result, resp.getOutputStream());
    }
}
