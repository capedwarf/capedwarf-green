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

import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Inject;

import org.jboss.lhotse.common.dto.DTOModel;
import org.jboss.lhotse.common.dto.DTOModelFactory;
import org.jboss.lhotse.common.dto.DefaultDTOModelFactory;
import org.jboss.lhotse.server.api.servlet.RequestHandler;

/**
 * Simple dto utils.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class DTOUtils
{
   private BeanManager beanManager;

   @Produces
   @ApplicationScoped
   public DTOModelFactory produce()
   {
      return new EnhancedDTOModelFactory();
   }

   private class EnhancedDTOModelFactory extends DefaultDTOModelFactory
   {
      @SuppressWarnings({"unchecked"})
      @Override
      protected <E extends Serializable> DTOModel createModelInternal(Class<E> clazz)
      {
         DTOModel model = super.createModelInternal(clazz);
         InjectionTarget it = beanManager.createInjectionTarget(beanManager.createAnnotatedType(model.getClass()));
         CreationalContext<RequestHandler> cc = beanManager.createCreationalContext(null);
         it.inject(model, cc);
         return model;
      }
   }

   @Inject
   public void setBeanManager(BeanManager beanManager)
   {
      this.beanManager = beanManager;
   }
}
