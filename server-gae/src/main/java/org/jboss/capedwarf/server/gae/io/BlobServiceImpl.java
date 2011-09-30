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

package org.jboss.capedwarf.server.gae.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.ByteRange;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import org.jboss.capedwarf.server.api.io.AbstractBlobService;
import org.jboss.capedwarf.server.api.io.Blob;

/**
 * GAE blob service impl.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class BlobServiceImpl extends AbstractBlobService
{
   private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
   private FileService fileService = FileServiceFactory.getFileService();

   protected Blob toBlobInternal(byte[] bytes)
   {
      return new BlobImpl(new com.google.appengine.api.datastore.Blob(bytes));
   }

   protected byte[] loadBytesInternal(String key, long startIndex, long endIndex)
   {
      BlobKey blobKey = new BlobKey(key);
      return blobstoreService.fetchData(blobKey, startIndex, endIndex);
   }

   protected void serveBytesInternal(String key, long start, long end, HttpServletResponse response) throws IOException
   {
      BlobKey blobKey = new BlobKey(key);
      ByteRange range = (end == Long.MAX_VALUE) ? new ByteRange(start) : new ByteRange(start, end);
      blobstoreService.serve(blobKey, range, response);
   }

   protected String storeBytesInternal(String mimeType, ByteBuffer buffer) throws IOException
   {
      AppEngineFile file = fileService.createNewBlobFile(mimeType);
      FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
      try
      {
         writeChannel.write(buffer);
      }
      finally
      {
         writeChannel.closeFinally();
      }
      BlobKey blobKey = fileService.getBlobKey(file);
      return blobKey.getKeyString();
   }
}
