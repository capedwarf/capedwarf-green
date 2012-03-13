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

package org.jboss.capedwarf.server.jee.env.impl;

import org.jboss.capedwarf.server.jee.env.Environment;

import javax.enterprise.context.ApplicationScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Arrays;

/**
 * Abstract environment
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public abstract class AbstractEnvironment implements Environment {
    protected static <T> T doLookup(String jndiName, Class<T> expectedType) throws Exception {
        Context context = new InitialContext();
        try {
            Object lookup = context.lookup(jndiName);
            return expectedType.cast(lookup);
        } finally {
            context.close();
        }
    }

    protected static <T> T doLookup(boolean allowNull, Class<T> expectedType, String... jndiNames) throws Exception {
        Context context = new InitialContext();
        try {
            for (String jndiName : jndiNames) {
                try {
                    Object lookup = context.lookup(jndiName);
                    return expectedType.cast(lookup);
                } catch (Throwable ignored) {
                }
            }
            if (allowNull == false)
                throw new IllegalArgumentException("No " + expectedType + " resource found: " + Arrays.toString(jndiNames));
            return null;
        } finally {
            context.close();
        }
    }
}
