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

package org.jboss.capedwarf.server.api.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.capedwarf.jpa.ProxyingWrapper;

/**
 * EMF info.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class EMFInfo
{
   private String unitName;
   private EMInjector emInjector;
   private ProxyingWrapper wrapper;

   public String getUnitName()
   {
      return unitName;
   }

   public EMInjector getEmInjector()
   {
      return emInjector;
   }

   public ProxyingWrapper getWrapper()
   {
      return wrapper;
   }

   @Inject
   public void setUnitName(@PersistenceUnitName String unitName)
   {
      this.unitName = unitName;
   }

   @Inject
   public void setEmInjector(EMInjector emInjector)
   {
      this.emInjector = emInjector;
   }

   @Inject
   public void setWrapper(ProxyingWrapper wrapper)
   {
      this.wrapper = wrapper;
   }
}
