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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.validation.ValidatorFactory;

import org.jboss.lhotse.server.api.lifecycle.After;
import org.jboss.lhotse.server.api.lifecycle.Notification;
import org.jboss.lhotse.validation.api.ValidationListener;


/**
 * javax.validation + jpa.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class PersistenceValidation
{
   private ValidatorFactory factory;

   public void initialize(@Observes @After Notification<EntityManagerFactory> status)
   {
      applyListener();
   }

   protected void applyListener()
   {
      ValidationListener.setFactory(factory);
   }

   @Inject
   public void setFactory(ValidatorFactory factory)
   {
      this.factory = factory;
   }
}