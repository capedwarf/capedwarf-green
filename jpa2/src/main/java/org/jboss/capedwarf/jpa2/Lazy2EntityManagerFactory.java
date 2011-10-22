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

package org.jboss.capedwarf.jpa2;

import java.util.Map;
import javax.persistence.Cache;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import org.jboss.capedwarf.jpa.LazyEntityManagerFactory;

/**
 * JPA2 lazy EMF.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class Lazy2EntityManagerFactory extends LazyEntityManagerFactory implements EntityManagerFactory
{
   public Lazy2EntityManagerFactory(String puName)
   {
      super(puName);
   }

   @Override
   protected EntityManagerFactory getDelegate()
   {
      return super.getDelegate();
   }

   public CriteriaBuilder getCriteriaBuilder()
   {
      return getDelegate().getCriteriaBuilder();
   }

   public Metamodel getMetamodel()
   {
      return getDelegate().getMetamodel();
   }

   public Map<String, Object> getProperties()
   {
      return getDelegate().getProperties();
   }

   public Cache getCache()
   {
      return getDelegate().getCache();
   }

   public PersistenceUnitUtil getPersistenceUnitUtil()
   {
      return getDelegate().getPersistenceUnitUtil();
   }
}
