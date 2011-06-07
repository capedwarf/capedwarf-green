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

package org.jboss.capedwarf.server.api.ui;

import java.util.Enumeration;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 * Command controller -- request scoped.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class Command
{
   protected static final String HOME = "home.cdi";
   private transient HttpServletRequest req;

   @SuppressWarnings({"unchecked"})
   protected String getParameter(String form, String key)
   {
      String suffix = form + ":" + key;
      Enumeration<String> names = req.getParameterNames();
      while (names.hasMoreElements())
      {
         String name = names.nextElement();
         if (name.endsWith(suffix))
            return req.getParameter(name);
      }
      return null;
   }

   protected Long getLong(String form, String key)
   {
      String sid = getParameter(form, key);
      return (sid != null) ? Long.parseLong(sid) : null;
   }

   @Inject
   public void setReq(HttpServletRequest req)
   {
      this.req = req;
   }
}
