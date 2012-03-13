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

package org.jboss.capedwarf.server.api.dao.impl;

import org.jboss.capedwarf.server.api.dao.StatelessDAO;
import org.jboss.capedwarf.server.api.domain.AbstractEntity;
import org.jboss.capedwarf.server.api.persistence.StatelessAdapter;

import java.io.Serializable;

/**
 * API bridge between adapter and DAO.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
final class StatelessAdapter2DAOBridge<T extends AbstractEntity> implements StatelessDAO<T> {
    private final StatelessAdapter adapter;

    StatelessAdapter2DAOBridge(StatelessAdapter adapter) {
        if (adapter == null)
            throw new IllegalArgumentException("Null adapter");
        this.adapter = adapter;
    }

    public Long insert(T entity) {
        return adapter.insert(entity);
    }

    public void update(T entity) {
        adapter.update(entity);
    }

    public void delete(T entity) {
        adapter.delete(entity);
    }

    @SuppressWarnings({"unchecked"})
    public T get(Class entityClass, Serializable id) {
        return (T) adapter.get(entityClass, id);
    }

    public void refresh(T entity) {
        adapter.refresh(entity);
    }

    public void initialize(T proxy) {
        adapter.initialize(proxy);
    }

    public void close() {
        adapter.close();
    }

    @Override
    public int hashCode() {
        return adapter.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return adapter.equals(obj);
    }

    @Override
    public String toString() {
        return adapter.toString();
    }
}
