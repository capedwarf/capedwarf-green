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

import java.io.Serializable;

/**
 * Provide stateless view of EntityManager / Session.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface StatelessAdapter {
    /**
     * Close adapter.
     */
    void close();

    /**
     * Insert a row.
     *
     * @param entity a new transient instance
     * @return entity's id
     */
    Long insert(Object entity);

    /**
     * Update a row.
     *
     * @param entity a detached entity instance
     */
    void update(Object entity);

    /**
     * Delete a row.
     *
     * @param entity a detached entity instance
     */
    void delete(Object entity);

    /**
     * Retrieve a row.
     *
     * @param entityClass the entity class
     * @param id          the id
     * @return a detached entity instance
     */
    <T> T get(Class<T> entityClass, Serializable id);

    /**
     * Refresh the entity instance state from the database.
     *
     * @param entity The entity to be refreshed.
     */
    void refresh(Object entity);

    /**
     * Force initialization of a proxy or persistent collection.
     * <p/>
     * Note: This only ensures intialization of a proxy object or collection;
     * it is not guaranteed that the elements INSIDE the collection will be initialized/materialized.
     *
     * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
     */
    void initialize(Object proxy);
}
