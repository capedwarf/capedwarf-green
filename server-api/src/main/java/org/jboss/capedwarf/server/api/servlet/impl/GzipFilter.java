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

package org.jboss.capedwarf.server.api.servlet.impl;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;


import org.jboss.capedwarf.common.Constants;
import org.jboss.capedwarf.common.serialization.GzipOptionalSerializator;

/**
 * Classic gzip filter.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GzipFilter implements Filter
{
   public void init(FilterConfig filterConfig) throws ServletException
   {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
   {
      if (request instanceof HttpServletRequest && response instanceof HttpServletResponse)
      {
         HttpServletRequest req = (HttpServletRequest) request;
         HttpServletResponse resp = (HttpServletResponse) response;

         String ignoreGzipHeader = req.getHeader(Constants.IGNORE_GZIP);
         boolean ignoreGzip = Boolean.parseBoolean(ignoreGzipHeader);
         if (ignoreGzip)
         {
            GzipOptionalSerializator.disableGzip();
            try
            {
               chain.doFilter(req, resp);   
            }
            finally
            {
               GzipOptionalSerializator.enableGzip();
            }
         }
         else
         {
            chain.doFilter(req, resp);
         }
      }
   }

   public void destroy()
   {
   }
}
