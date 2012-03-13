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

package org.jboss.capedwarf.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

/**
 * SQLite model.
 *
 * @param <T> exact entity type
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
interface SQLiteEntityModel<T> {
    /**
     * Get table name.
     *
     * @return the table name
     */
    String getTable();

    /**
     * Key name.
     *
     * @return the key name
     */
    String getKey();

    /**
     * Get columns.
     *
     * @return the columns
     */
    String[] getKeyAndColumns();

    /**
     * Get columns description.
     *
     * @return the columns description
     */
    String[] getColumnsDescription();

    /**
     * The key value.
     *
     * @param entity the current entity
     * @return key value
     */
    Long getKeyValue(T entity);

    /**
     * Set key value.
     *
     * @param entity the current entity
     * @param key    value
     */
    void setKeyValue(T entity, Long key);

    /**
     * Read results from cursor.
     *
     * @param cursor the cursor
     * @return results
     */
    List<T> readCursor(Cursor cursor);

    /**
     * Get content values.
     *
     * @param entity the entity
     * @return content values
     */
    ContentValues getContentValues(T entity);

    /**
     * Get on insert.
     *
     * @return on insert
     */
    EntityListener onInsert();

    /**
     * Get on load.
     *
     * @return on insert
     */
    EntityListener onLoad();

    /**
     * Get on update.
     *
     * @return on insert
     */
    EntityListener onUpdate();

    /**
     * Get on delete.
     *
     * @return on insert
     */
    EntityListener onDelete();
}