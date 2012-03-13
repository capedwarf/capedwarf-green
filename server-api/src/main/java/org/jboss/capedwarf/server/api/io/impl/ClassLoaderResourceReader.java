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

package org.jboss.capedwarf.server.api.io.impl;

import org.jboss.capedwarf.server.api.io.ResourceReader;
import org.jboss.seam.solder.resourceLoader.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Set;

/**
 * Read resources.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class ClassLoaderResourceReader implements ResourceReader {
    @Produces
    @Resource("")
    public byte[] getResource(InjectionPoint ip) {
        return getResource(getName(ip));
    }

    public byte[] getResource(String resource) {
        try {
            ClassLoader cl = getClass().getClassLoader();
            URL url = cl.getResource(resource);
            if (url == null)
                return null;

            InputStream is = url.openStream();
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int b;
                while ((b = is.read()) >= 0) {
                    baos.write(b);
                }
                baos.flush();
                baos.close();
                return baos.toByteArray();
            } finally {
                is.close();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private String getName(InjectionPoint ip) {
        Set<Annotation> qualifiers = ip.getQualifiers();
        for (Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Resource.class)) {
                return ((Resource) qualifier).value();
            }
        }
        throw new IllegalArgumentException("Injection point " + ip + " does not have @Resource qualifier");
    }
}
