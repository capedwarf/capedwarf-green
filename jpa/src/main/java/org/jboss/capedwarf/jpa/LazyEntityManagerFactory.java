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

package org.jboss.capedwarf.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Map;

/**
 * Lazy EMF
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class LazyEntityManagerFactory implements EntityManagerFactory {
    private String puName;
    private volatile EntityManagerFactory delegate;

    public LazyEntityManagerFactory(String puName) {
        if (puName == null)
            throw new IllegalArgumentException("Null PU name");
        this.puName = puName;
    }

    protected EntityManagerFactory getDelegate() {
        if (delegate == null) {
            synchronized (this) {
                if (delegate == null)
                    delegate = Persistence.createEntityManagerFactory(puName);
            }
        }
        return delegate;
    }

    public EntityManager createEntityManager() {
        return getDelegate().createEntityManager();
    }

    public EntityManager createEntityManager(Map map) {
        return getDelegate().createEntityManager(map);
    }

    public void close() {
        EntityManagerFactory temp = delegate;
        if (temp != null) {
            temp.close();
        }
    }

    public boolean isOpen() {
        return getDelegate().isOpen();
    }
}
