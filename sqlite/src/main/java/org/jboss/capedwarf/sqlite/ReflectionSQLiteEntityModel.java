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
import org.jboss.capedwarf.common.sql.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Reflection based sqlite entity model.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ReflectionSQLiteEntityModel<T> extends AbstractSQLiteEntityModel<T> {
    private String key;
    private Map<String, Property> propertys;

    public ReflectionSQLiteEntityModel(Class<T> entityClass) throws Exception {
        super(entityClass);
        this.propertys = new LinkedHashMap<String, Property>();
        init();
    }

    protected void init() throws Exception {
        OnInsert onInsert = entityClass.getAnnotation(OnInsert.class);
        if (onInsert != null)
            putListener(OnInsert.class.getSimpleName(), loadEntityListener(onInsert.value()));

        OnLoad onLoad = entityClass.getAnnotation(OnLoad.class);
        if (onLoad != null)
            putListener(OnLoad.class.getSimpleName(), loadEntityListener(onLoad.value()));

        OnUpdate onUpdate = entityClass.getAnnotation(OnUpdate.class);
        if (onUpdate != null)
            putListener(OnUpdate.class.getSimpleName(), loadEntityListener(onUpdate.value()));

        OnDelete onDelete = entityClass.getAnnotation(OnDelete.class);
        if (onDelete != null)
            putListener(OnDelete.class.getSimpleName(), loadEntityListener(onDelete.value()));

        Method[] ms = entityClass.getMethods();
        for (Method m : ms) {
            Column column = m.getAnnotation(Column.class);
            if (column != null) {
                boolean setter = false;
                String upper = null;
                Class<?> type = null;

                String methodName = m.getName();
                Class<?>[] pt = m.getParameterTypes();
                if (pt.length == 0) // getter?
                {
                    if (methodName.startsWith("get")) {
                        upper = methodName.substring(3);
                        type = m.getReturnType();
                    } else if (methodName.startsWith("is")) {
                        upper = methodName.substring(2);
                        type = m.getReturnType();
                    }
                }
                if (upper == null && methodName.startsWith("set") && pt.length == 1) {
                    upper = methodName.substring(3);
                    type = pt[0];
                    setter = true;
                }

                if (upper == null)
                    throw new IllegalArgumentException("Column is not on getter/setter: " + m);

                // check
                if (setter)
                    entityClass.getMethod((type == boolean.class ? "is" : "get") + upper);
                else
                    entityClass.getMethod("set" + upper, type);

                String name = column.name().length() > 0 ? column.name() : upper.toLowerCase();
                propertys.put(name, new Property(upper, type, column.defaultValue()));
            }

            readKey(m);
        }

        if (getKey() == null)
            throw new IllegalArgumentException("Missing @Key: " + entityClass);

        if (propertys.isEmpty())
            throw new IllegalArgumentException("Empty propertys: " + entityClass);
    }

    protected void readKey(Method m) throws NoSuchMethodException {
        Key key = m.getAnnotation(Key.class);
        if (key != null) {
            if (this.key != null)
                throw new IllegalArgumentException("Key already set: " + this.key);

            String keyName = key.value();

            Method keyGetter = entityClass.getMethod("get" + toCapitalize(keyName));
            Class<?> rt = keyGetter.getReturnType();
            if ((rt == Long.class || rt == long.class) == false)
                throw new IllegalArgumentException("Only long/Long keys allowed.");

            this.key = keyName;
        }
    }

    protected static String toCapitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public String getKey() {
        return key;
    }

    public Set<String> getColumns() {
        return propertys.keySet();
    }

    public String[] getColumnsDescription() {
        String[] result = new String[propertys.size()];
        int i = 0;
        for (Map.Entry<String, Property> entry : propertys.entrySet()) {
            Property value = entry.getValue();
            result[i++] = (entry.getKey() + " " + value.getSQLType()) + value.getDefaultValue();
        }
        return result;
    }

    public Long getKeyValue(T entity) {
        if (key == null || entity == null)
            return null;

        try {
            Method m = entityClass.getMethod("get" + toCapitalize(key));
            return (Long) m.invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setKeyValue(T entity, Long id) {
        if (key == null || entity == null)
            return;

        try {
            Method m = entityClass.getMethod("set" + toCapitalize(key), Long.class);
            m.invoke(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<T> readValues(Cursor cursor) {
        List<T> results = new ArrayList<T>();
        do {
            try {
                T instance = entityClass.newInstance();
                String[] columns = cursor.getColumnNames();
                for (int i = 0; i < columns.length; i++) {
                    String column = columns[i];

                    // read Pk
                    if (column.equals(getKey())) {
                        setKeyValue(instance, cursor.getLong(i));
                        continue;
                    }

                    Property property = propertys.get(column);
                    if (property == null)
                        throw new IllegalArgumentException("No such matching property: " + column);

                    Class<?> type = property.type;

                    Object value;
                    if (cursor.isNull(i))
                        value = null;
                    else if (type == String.class)
                        value = cursor.getString(i);
                    else if (type == Byte.class || type == byte.class)
                        value = cursor.getInt(i);
                    else if (type == Short.class || type == short.class)
                        value = cursor.getShort(i);
                    else if (type == Integer.class || type == int.class)
                        value = cursor.getInt(i);
                    else if (type == Long.class || type == long.class)
                        value = cursor.getLong(i);
                    else if (type == Float.class || type == float.class)
                        value = cursor.getFloat(i);
                    else if (type == Double.class || type == double.class)
                        value = cursor.getDouble(i);
                    else if (type == Boolean.class || type == boolean.class)
                        value = (cursor.getInt(i) > 0);
                    else if (type == byte[].class)
                        value = cursor.getBlob(i);
                    else if (Enum.class.isAssignableFrom(type))
                        value = toEnum(type, cursor.getInt(i));
                    else
                        throw new IllegalArgumentException("Cannot support value type: " + property);

                    property.setValue(instance, value);
                }

                results.add(instance);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        while (cursor.moveToNext());

        return results;
    }

    public ContentValues getContentValues(T entity) {
        if (entity == null)
            return new ContentValues(0);

        ContentValues cv = new ContentValues(propertys.size());
        for (Map.Entry<String, Property> entry : propertys.entrySet()) {
            String key = entry.getKey();
            Property property = entry.getValue();
            Object value = property.getValue(entity);

            if (value == null)
                cv.putNull(key);
            else {
                if (value instanceof String)
                    cv.put(key, (String) value);
                else if (value instanceof Byte)
                    cv.put(key, (Byte) value);
                else if (value instanceof Short)
                    cv.put(key, (Short) value);
                else if (value instanceof Integer)
                    cv.put(key, (Integer) value);
                else if (value instanceof Long)
                    cv.put(key, (Long) value);
                else if (value instanceof Float)
                    cv.put(key, (Float) value);
                else if (value instanceof Double)
                    cv.put(key, (Double) value);
                else if (value instanceof Boolean)
                    cv.put(key, (Boolean) value);
                else if (value instanceof byte[])
                    cv.put(key, (byte[]) value);
                else if (value instanceof Enum)
                    cv.put(key, ((Enum) value).ordinal());
                else
                    throw new IllegalArgumentException("Cannot support value type: " + property);
            }
        }
        return cv;
    }

    private class Property {
        private String upper;
        private Class<?> type;
        private Method getter;
        private Method setter;
        private String defaultValue;

        private Property(String upper, Class<?> type, String defaultValue) {
            this.upper = upper;
            this.type = type;
            if (defaultValue != null && defaultValue.length() > 0) {
                this.defaultValue = " DEFAULT " + defaultValue;
            }
        }

        public Object getValue(T entity) {
            try {
                if (getter == null)
                    getter = entityClass.getMethod((type == boolean.class ? "is" : "get") + upper);

                return getter.invoke(entity);
            } catch (Exception e) {
                throw new RuntimeException(toString() + " -> " + entity, e);
            }
        }

        public void setValue(T instance, Object value) throws Exception {
            try {
                if (setter == null)
                    setter = entityClass.getMethod("set" + upper, type);

                setter.invoke(instance, value);
            } catch (Exception e) {
                throw new IllegalArgumentException(toString() + " -> " + instance + "::" + value, e);
            }
        }

        public String getSQLType() {
            return SQLiteTypes.getSQLType(type).name();
        }

        public String getDefaultValue() {
            return defaultValue != null ? defaultValue : "";
        }

        public String toString() {
            return entityClass + "$" + upper.toLowerCase() + "#" + type;
        }
    }
}
