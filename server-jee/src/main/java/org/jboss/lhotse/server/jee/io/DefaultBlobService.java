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

package org.jboss.lhotse.server.jee.io;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletResponse;

import org.jboss.lhotse.server.api.io.AbstractBlobService;
import org.jboss.lhotse.server.api.io.Blob;

/**
 * Default blob service.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class DefaultBlobService extends AbstractBlobService
{
   protected Blob toBlobInternal(byte[] bytes)
   {
      return null; // TODO
   }

   @Override
   protected byte[] loadBytesInternal(String key, long startIndex, long endIndex)
   {
      return new byte[0]; // TODO
   }

   @Override
   protected void serveBytesInternal(String key, long start, long end, HttpServletResponse response) throws IOException
   {
      // TODO
   }

   @Override
   protected String storeBytesInternal(String mimeType, byte[] bytes) throws IOException
   {
      return null; // TODO
   }
}