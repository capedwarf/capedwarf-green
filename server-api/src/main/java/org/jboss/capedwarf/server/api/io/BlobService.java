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
import javax.servlet.http.HttpServletResponse;

/**
 * byte[] handling service.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface BlobService extends BlobTransformer
{
   /**
    * Load the bytes.
    *
    * @param key the blob key
    * @return bytes or null if no such blob found
    */
   byte[] loadBytes(String key);

   /**
    * Load the bytes.
    *
    * @param key the blob key
    * @param startIndex start index of data to fetch.
    * @param endIndex end index (inclusive) of data to fetch.
    * @return bytes or null if no such blob found
    */
   byte[] loadBytes(String key, long startIndex, long endIndex);

   /**
    * Serve bytes directly into repsonse.
    *
    * @param key the blob key
    * @param response the http response
    * @throws IOException for any I/O error
    */
   void serveBytes(String key, HttpServletResponse response) throws IOException;

   /**
    * Serve bytes directly into repsonse.
    *
    * @param key the blob key
    * @param start start index of data to fetch.
    * @param response the http response
    * @throws IOException for any I/O error
    */
   void serveBytes(String key, long start, HttpServletResponse response) throws IOException;

   /**
    * Serve bytes directly into repsonse.
    *
    * @param key the blob key
    * @param start start index of data to fetch.
    * @param end end index (inclusive) of data to fetch.
    * @param response the http response
    * @throws IOException for any I/O error
    */
   void serveBytes(String key, long start, long end, HttpServletResponse response) throws IOException;

   /**
    * Store bytes.
    * See http://www.w3schools.com/media/media_mimeref.asp.
    *
    * @param mimeType the mime type
    * @param bytes the bytes
    * @return the blob key or null if cannot store
    * @throws IOException for any I/O error
    */
   String storeBytes(String mimeType, byte[] bytes) throws IOException;
}
