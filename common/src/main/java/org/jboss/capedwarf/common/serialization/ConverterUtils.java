/*
* JBoss, Home of Professional Open Source
* Copyright $today.year Red Hat Inc. and/or its affiliates and other
* contributors as indicated by the @author tags. All rights reserved.
* See the copyright.txt in the distribution for a full listing of
* individual contributors.
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

package org.jboss.capedwarf.common.serialization;

import java.lang.reflect.Method;

/**
 * Converter helper.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ConverterUtils {
    /**
     * Convert to value from string.
     *
     * @param rt    return class
     * @param value the string value
     * @return converted value
     * @throws Throwable for any error
     */
    public static Object toValue(Class<?> rt, String value) throws Throwable {
        if (String.class == rt) {
            return value;
        } else if (Boolean.class == rt) { // Boolean.valueOf("some-gae-crap") == false
            if ("true".equalsIgnoreCase(value))
                return Boolean.TRUE;
            else if ("false".equalsIgnoreCase(value))
                return Boolean.FALSE;
            else
                throw new IllegalArgumentException("Server side exception? - " + value);
        } else if (Enum.class.isAssignableFrom(rt)) {
            //noinspection unchecked
            return Enum.valueOf((Class<Enum>) rt, value);
        } else {
            Method valueOf = boxedClass(rt).getMethod("valueOf", value.getClass());
            return valueOf.invoke(null, value);
        }
    }

    /**
     * Get boxede class.
     *
     * @param type the type to box
     * @return boxed class
     */
    public static Class<?> boxedClass(Class<?> type) {
        if (type.isPrimitive() == false) {
            return type;
        } else if (type.equals(Boolean.TYPE)) {
            return Boolean.class;
        } else if (type.equals(Character.TYPE)) {
            return Character.class;
        } else if (type.equals(Byte.TYPE)) {
            return Byte.class;
        } else if (type.equals(Short.TYPE)) {
            return Short.class;
        } else if (type.equals(Integer.TYPE)) {
            return Integer.class;
        } else if (type.equals(Long.TYPE)) {
            return Long.class;
        } else if (type.equals(Float.TYPE)) {
            return Float.class;
        } else if (type.equals(Double.TYPE)) {
            return Double.class;
        } else if (type.equals(Void.TYPE)) {
            return Void.class;
        } else {
            // Vagaries of if/else statement, can't be reached ;-)
            return type;
        }
    }
}
