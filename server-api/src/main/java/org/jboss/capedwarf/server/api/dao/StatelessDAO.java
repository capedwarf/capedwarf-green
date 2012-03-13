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

package org.jboss.capedwarf.server.api.dao;

import org.jboss.capedwarf.server.api.domain.AbstractEntity;

import java.io.Serializable;

/**
 * Stateless DAO.
 *
 * @param <T> exact dao type
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface StatelessDAO<T extends AbstractEntity> {
    /**
     * Insert a row.
     *
     * @param entity a new transient instance
     * @return entity's id
     */
    Long insert(T entity);

    /**
     * Update a row.
     *
     * @param entity a detached entity instance
     */
    void update(T entity);

    /**
     * Delete a row.
     *
     * @param entity a detached entity instance
     */
    void delete(T entity);

    /**
     * Retrieve a row.
     *
     * @param entityClass the entity class
     * @param id          the id
     * @return a detached entity instance
     */
    T get(Class<T> entityClass, Serializable id);

    /**
     * Refresh the entity instance state from the database.
     *
     * @param entity The entity to be refreshed.
     */
    void refresh(T entity);

    /**
     * Force initialization of a proxy.
     * <p/>
     * Note: This only ensures intialization of a proxy object.
     *
     * @param proxy a persistable object, proxy
     */
    void initialize(T proxy);

    /**
     * Close DAO.
     * (release underlying adapter)
     */
    void close();
}
