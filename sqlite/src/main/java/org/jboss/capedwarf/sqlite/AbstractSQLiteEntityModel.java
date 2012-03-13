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

import android.database.Cursor;
import org.jboss.capedwarf.common.sql.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract sqlite entity model.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractSQLiteEntityModel<T> implements SQLiteEntityModel<T> {
    protected Class<T> entityClass;
    private Map<String, EntityListener> listeners;
    private volatile String table;
    private volatile String[] keyAndColumns;

    protected AbstractSQLiteEntityModel(Class<T> entityClass) throws Exception {
        if (entityClass == null)
            throw new IllegalArgumentException("Null entity class");
        this.entityClass = entityClass;
        this.listeners = new ConcurrentHashMap<String, EntityListener>();
    }

    public String getTable() {
        if (table == null) {
            Table t = entityClass.getAnnotation(Table.class);
            table = (t != null && t.name().length() > 0) ? t.name() : entityClass.getSimpleName();
        }
        return table;
    }

    protected abstract Set<String> getColumns();

    public String[] getKeyAndColumns() {
        if (keyAndColumns == null) {
            Set<String> all = new LinkedHashSet<String>();
            all.add(getKey());
            all.addAll(getColumns());
            keyAndColumns = all.toArray(new String[all.size()]);
        }
        return keyAndColumns;
    }

    static Cursor toFirst(Cursor cursor) {
        if (cursor == null) {
            return null;
        } else if (cursor.moveToFirst() == false) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public List<T> readCursor(Cursor cursor) {
        cursor = toFirst(cursor);

        if (cursor == null)
            return Collections.emptyList();

        try {
            return readValues(cursor);
        } finally {
            cursor.close();
        }
    }

    protected abstract List<T> readValues(Cursor cursor);

    protected EntityListener loadEntityListener(String className) {
        try {
            Class<?> elc = getClass().getClassLoader().loadClass(className);
            Object result = elc.newInstance();
            if (EntityListener.class.isInstance(result) == false)
                throw new IllegalArgumentException("Not an EntityListener: " + className);

            return (EntityListener) result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void putListener(String key, EntityListener listener) {
        if (listener == null)
            listeners.remove(key);
        else
            listeners.put(key, listener);
    }

    public EntityListener onInsert() {
        return listeners.get(OnInsert.class.getSimpleName());
    }

    public EntityListener onLoad() {
        return listeners.get(OnLoad.class.getSimpleName());
    }

    public EntityListener onUpdate() {
        return listeners.get(OnUpdate.class.getSimpleName());
    }

    public EntityListener onDelete() {
        return listeners.get(OnDelete.class.getSimpleName());
    }

    protected Object toEnum(Class<?> enumClass, int ordinal) {
        try {
            Method m = enumClass.getMethod("values");
            Object[] values = (Object[]) m.invoke(null);
            return values[ordinal];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return entityClass.getSimpleName();
    }
}
