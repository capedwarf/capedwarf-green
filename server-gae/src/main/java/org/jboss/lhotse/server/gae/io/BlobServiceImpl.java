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

package org.jboss.lhotse.server.gae.io;

import javax.enterprise.context.ApplicationScoped;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import org.jboss.lhotse.server.api.io.BlobService;

/**
 * GAE blob service impl.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class BlobServiceImpl implements BlobService
{
   private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

   public byte[] loadBytes(String key)
   {
      BlobKey bk = new BlobKey(key);
      return blobstoreService.fetchData(bk, 0, Long.MAX_VALUE); // TODO
   }

   public String storeBytes(byte[] bytes)
   {
      return null;
   }
}
