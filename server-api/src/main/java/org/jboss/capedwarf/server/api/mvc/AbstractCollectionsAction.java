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

import org.jboss.capedwarf.common.dto.DTOModel;
import org.jboss.capedwarf.common.serialization.GzipOptionalSerializator;
import org.jboss.capedwarf.common.serialization.Serializator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


/**
 * Abstract collections action.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractCollectionsAction<T extends Serializable> extends AbstractAction
{
   private Serializator serializator;

   protected void doInitialize(ServletContext context)
   {
      super.doInitialize(context);
      serializator = new GzipOptionalSerializator(getSerializator());
   }

   protected abstract Serializator getSerializator();

   protected abstract DTOModel<T> getDtoModel();

   /**
    * Push topics to response.
    *
    * @param elements the elements
    * @param resp the response
    * @throws java.io.IOException for any I/O error
    */
   @SuppressWarnings({"unchecked"})
   protected void toDTO(Iterable<T> elements, HttpServletResponse resp) throws IOException
   {
      try
      {
         List dtos = new ArrayList();
         for (T t : elements)
         {
            Object dto = getDtoModel().toDTO(t);
            dtos.add(dto);
         }
         prepareResponse(resp);
         serializator.serialize(dtos, resp.getOutputStream());
      }
      catch (Exception e)
      {
         log.log(Level.WARNING, "Error handling collections.", e);
         resp.sendError(HttpServletResponse.SC_SEE_OTHER);
      }
   }
}
