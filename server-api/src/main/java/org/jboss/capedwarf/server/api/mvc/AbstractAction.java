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

import org.jboss.capedwarf.common.serialization.ConverterUtils;
import org.jboss.capedwarf.common.serialization.GzipOptionalSerializator;
import org.jboss.capedwarf.server.api.servlet.AbstractRequestHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Abstract action.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractAction extends AbstractRequestHandler {
    protected Logger log = Logger.getLogger(getClass().getName());

    protected void doInitialize(ServletContext context) {
    }

    /**
     * Get parameter.
     *
     * @param req  the request
     * @param name the name
     * @return parameter
     */
    protected static String getParameter(HttpServletRequest req, String name) {
        return getParameter(req, name, true);
    }

    /**
     * Get parameter.
     *
     * @param req      the request
     * @param name     the name
     * @param required is the parameter required
     * @return parameter
     */
    protected static String getParameter(HttpServletRequest req, String name, boolean required) {
        String value = req.getParameter(name);
        if (value == null && required)
            throw new IllegalArgumentException("Missing parameter " + name);

        return value;
    }

    /**
     * Parse long parameter.
     *
     * @param req  the request
     * @param name the parameter name
     * @return parsed value or exception if no such value
     */
    protected static long parseLong(HttpServletRequest req, String name) {
        String value = getParameter(req, name);
        return Long.parseLong(value);
    }

    /**
     * Parse long parameter.
     *
     * @param req          the request
     * @param name         the parameter name
     * @param defaultValue the default value
     * @return parsed value or exception if no such value
     */
    protected static long parseLong(HttpServletRequest req, String name, long defaultValue) {
        String value = getParameter(req, name, false);
        return (value != null) ? Long.parseLong(value) : defaultValue;
    }

    /**
     * Parse int parameter.
     *
     * @param req  the request
     * @param name the parameter name
     * @return parsed value or exception if no such value
     */
    protected static int parseInt(HttpServletRequest req, String name) {
        String value = getParameter(req, name);
        return Integer.parseInt(value);
    }

    /**
     * Parse int.
     *
     * @param req          the request
     * @param name         the parameter name
     * @param defaultValue the default value
     * @return parsed value or default if no such value
     */
    protected int parseInt(HttpServletRequest req, String name, int defaultValue) {
        String value = req.getParameter(name);
        return (value != null) ? Integer.parseInt(value) : defaultValue;
    }

    /**
     * Parse boolean.
     *
     * @param req          the request
     * @param name         the parameter name
     * @param defaultValue the default value
     * @return parsed booelan value or default if no such parameter
     */
    protected boolean parseBoolean(HttpServletRequest req, String name, boolean defaultValue) {
        String value = req.getParameter(name);
        return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
    }

    /**
     * Parse enum.
     *
     * @param req          the request
     * @param name         the parameter name
     * @param defaultValue the default value
     * @param values       the values
     * @return parsed enum value or first enum value if no such param
     */
    protected static <T extends Enum> T parseEnum(HttpServletRequest req, String name, T defaultValue, T[] values) {
        String type = getParameter(req, name);
        int ordinal = (type != null) ? Integer.parseInt(type) : -1;
        return (ordinal < 0 || ordinal >= values.length) ? defaultValue : values[ordinal];
    }

    /**
     * Parse comma list.
     *
     * @param req   the request
     * @param name  the parameter name
     * @param clazz the expected class
     * @return parsed list
     * @throws IOException for any error
     */
    protected <T> List<T> parseList(HttpServletRequest req, String name, Class<T> clazz) throws IOException {
        try {
            List<T> result = new ArrayList<T>();
            String value = getParameter(req, name, false);
            if (value != null) {
                String[] split = value.split(",");
                for (String s : split) {
                    Object element = ConverterUtils.toValue(clazz, s);
                    result.add(clazz.cast(element));
                }
            }
            return result;
        } catch (Throwable t) {
            IOException ioe = new IOException();
            ioe.initCause(t);
            throw ioe;
        }
    }

    /**
     * Get request input stream.
     *
     * @param req the request
     * @return request stream
     * @throws IOException for any I/O error
     */
    protected InputStream parseInputStream(HttpServletRequest req) throws IOException {
        return parseInputStream(req, GzipOptionalSerializator.isGzipEnabled());
    }

    /**
     * Get request input stream.
     *
     * @param req  the request
     * @param gzip do we un-gzip stream
     * @return request stream
     * @throws IOException for any I/O error
     */
    protected InputStream parseInputStream(HttpServletRequest req, boolean gzip) throws IOException {
        InputStream is = req.getInputStream();
        return (gzip) ? new GZIPInputStream(is) : is;
    }

    /**
     * Prepare response.
     *
     * @param resp the response
     */
    protected void prepareResponse(HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
    }

    /**
     * Write result.
     *
     * @param resp   the response
     * @param result the result
     * @throws IOException for any IO error
     */
    protected void writeResult(HttpServletResponse resp, Object result) throws IOException {
        if (result instanceof Iterable) {
            writeResults(resp, (Iterable) result);
        } else {
            prepareResponse(resp);

            Writer writer = resp.getWriter();
            writer.write(String.valueOf(result));
            writer.flush();
        }
    }

    /**
     * Write results.
     *
     * @param resp    the response
     * @param results the results
     * @throws IOException for any IO error
     */
    protected void writeResults(HttpServletResponse resp, Iterable results) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (Object arg : results) {
            if (builder.length() > 0)
                builder.append(",");
            builder.append(arg);
        }
        writeResult(resp, builder);
    }
}
