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

package org.jboss.capedwarf.jpa2;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jboss.capedwarf.jpa.EntityManagerProvider;
import org.jboss.capedwarf.jpa.ProxyingEntityManagerFactory;

/**
 * JPA2 proxying entity manager factory.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class NewProxyingEntityManagerFactory extends ProxyingEntityManagerFactory
{
   public NewProxyingEntityManagerFactory(EntityManagerFactory delegate)
   {
      super(delegate);
   }

   public NewProxyingEntityManagerFactory(EntityManagerFactory delegate, EntityManagerProvider provider)
   {
      super(delegate, provider);
   }

   @Override
   protected EntityManager proxy(final EntityManager delegate)
   {
      return new NewProxyingEntityManager(delegate)
      {
         protected EntityManagerProvider getProvider()
         {
            return getProviderInternal();
         }
      };
   }
}
