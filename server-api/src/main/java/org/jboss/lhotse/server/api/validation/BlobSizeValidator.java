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

package org.jboss.lhotse.server.api.validation;

import org.jboss.lhotse.server.api.io.Blob;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Blob size validator.
 * 
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class BlobSizeValidator implements ConstraintValidator<BlobSize, Object>
{
   private int min;
   private int max;

   public void initialize(BlobSize bs)
   {
      min = bs.min();
      max = bs.max();
   }

   public boolean isValid(Object value, ConstraintValidatorContext context)
   {
      if (value == null)
         return true;

      if (value instanceof Blob == false)
         return false;

      Blob blob = (Blob) value;
      byte[] bytes = blob.getBytes();
      int length = bytes.length;
      return length >= min && length <= max; 
   }
}