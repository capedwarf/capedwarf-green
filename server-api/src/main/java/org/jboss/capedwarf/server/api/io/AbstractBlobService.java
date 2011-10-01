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

package org.jboss.capedwarf.server.api.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.jboss.capedwarf.common.serialization.GzipOptionalSerializator;

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

   public void serveBytes(String key, OutputStream outstream) throws IOException
   {
      serveBytes(key, 0, outstream);
   }

   public void serveBytes(String key, long start, OutputStream outstream) throws IOException
   {
      serveBytes(key, start, Long.MAX_VALUE, outstream);
   }

   public void serveBytes(String key, long start, long end, OutputStream outstream) throws IOException
   {
      byte[] bytes = loadBytes(key, start, end);
      if (bytes != null)
      {
         if (GzipOptionalSerializator.isGzipDisabled())
         {
            outstream.write(bytes);
         }
         else
         {
            GZIPOutputStream gzip = new GZIPOutputStream(outstream);
            gzip.write(bytes);
            gzip.finish();
         }
      }
   }

   public void serveBytes(String key, long start, long end, HttpServletResponse respose) throws IOException
   {
      serveBytes(key, start, end, respose.getOutputStream());
   }

   public String storeBytes(String mimeType, byte[] bytes) throws IOException
   {
      if (bytes == null)
         return null;

      return storeBytesInternal(mimeType, ByteBuffer.wrap(bytes));
   }

   public String storeBytes(String mimeType, ByteBuffer buffer) throws IOException
   {
      if (buffer == null)
         return null;

      return storeBytesInternal(mimeType, buffer);
   }

   protected abstract String storeBytesInternal(String mimeType, ByteBuffer buffer) throws IOException;
}
