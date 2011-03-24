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

package org.jboss.lhotse.server.api.mvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Result abstract action.
 *
 * @param <R> exact result type
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class ResultAbstractAction<R> extends AbstractAction
{
   private boolean suppressException = true;

   /**
    * Do handle request and response.
    *
    * @param req the request
    * @param resp the response
    * @return true if the action was successful, false otherwise
    * @throws javax.servlet.ServletException for any servlet error
    * @throws java.io.IOException for any I/O error
    */
   protected abstract R doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

   public void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      try
      {
         R result = doHandle(req, resp);
         doWriteResult(resp, result);
      }
      catch (ServletException e)
      {
         handle(resp, e);
      }
      catch (IOException e)
      {
         handle(resp, e);
      }
      catch (RuntimeException e)
      {
         handle(resp, e);
      }
   }

   protected void doWriteResult(HttpServletResponse resp, R result) throws IOException
   {
      writeResult(resp, result);
   }

   protected abstract R errorResult();

   private <T extends Throwable> void handle(HttpServletResponse resp, T t) throws T
   {
      if (suppressException)
      {
         try
         {
            log.log(Level.WARNING, "Ignoring exception.", t);
            doWriteResult(resp, errorResult());
         }
         catch (IOException ignored)
         {
         }
      }
      else
      {
         throw t;
      }
   }

   public void setSuppressException(boolean suppressException)
   {
      this.suppressException = suppressException;
   }
}
