/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.capedwarf.server.api.io.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.capedwarf.server.api.dao.ImageDAO;
import org.jboss.capedwarf.server.api.domain.AbstractImage;
import org.jboss.capedwarf.server.api.io.AbstractSimpleBlobService;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public abstract class AbstractDatabaseBlobService<T extends AbstractImage> extends AbstractSimpleBlobService
{
   private ImageDAO<T> imageDAO;

   protected byte[] loadBytesInternal(String key, long startIndex, long endIndex)
   {
      Long id = Long.parseLong(key);
      AbstractImage image = imageDAO.find(id);
      if (image != null)
      {
         long min = (endIndex == Long.MAX_VALUE) ? image.getLength() : endIndex;
         try
         {
            return image.read(startIndex, min);
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      return null;
   }

   /**
    * Create new image instance.
    *
    * @return new image instance
    */
   protected abstract T createImageInstance();

   protected String storeBytesInternal(String mimeType, ByteBuffer buffer) throws IOException
   {
      try
      {
         T image = createImageInstance();
         image.setMimeType(mimeType);
         byte[] array = buffer.array();
         image.write(array);
         image.setLength(array.length);
         imageDAO.save(image);
         return Long.toString(image.getId());
      }
      catch (Exception e)
      {
         IOException ioe = new IOException();
         ioe.initCause(e);
         throw ioe;
      }
   }

   @Inject
   public void setImageDAO(ImageDAO<T> imageDAO)
   {
      this.imageDAO = imageDAO;
   }
}
