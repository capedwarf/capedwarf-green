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

package org.jboss.lhotse.server.api.io;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstract byte[] handling service.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractBlobService implements BlobService
{
   public Blob toBlob(byte[] bytes)
   {
      if (bytes == null)
         return null;

      return toBlobInternal(bytes);
   }

   protected abstract Blob toBlobInternal(byte[] bytes);

   public byte[] loadBytes(String key)
   {
      return loadBytesInternal(key, 0, Long.MAX_VALUE);
   }

   public byte[] loadBytes(String key, long startIndex, long endIndex)
   {
      if (key == null || (startIndex > endIndex))
         return null;
      if (startIndex == endIndex)
         return new byte[0];

      return loadBytesInternal(key, startIndex, endIndex);
   }

   protected abstract byte[] loadBytesInternal(String key, long startIndex, long endIndex);

   public void serveBytes(String key, HttpServletResponse response) throws IOException
   {
      serveBytes(key, 0, response);
   }

   public void serveBytes(String key, long start, HttpServletResponse response) throws IOException
   {
      serveBytes(key, start, -1, response);
   }

   public void serveBytes(String key, long start, long end, HttpServletResponse response) throws IOException
   {
      if (key == null)
         return;

      serveBytesInternal(key, start, end, response);
   }

   protected abstract void serveBytesInternal(String key, long start, long end, HttpServletResponse response) throws IOException;

   public String storeBytes(String mimeType, byte[] bytes) throws IOException
   {
      if (bytes == null)
         return null;

      return storeBytesInternal(mimeType, bytes);
   }

   protected abstract String storeBytesInternal(String mimeType, byte[] bytes) throws IOException;
}
