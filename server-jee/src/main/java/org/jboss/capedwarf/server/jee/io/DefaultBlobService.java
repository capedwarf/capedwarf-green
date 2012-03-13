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

package org.jboss.capedwarf.server.jee.io;

import org.jboss.capedwarf.server.api.io.AbstractSimpleBlobService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Default blob service.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
@Alternative
public class DefaultBlobService extends AbstractSimpleBlobService {
    private volatile File dataDir;

    protected File getDataDir() {
        if (dataDir == null) {
            synchronized (this) {
                if (dataDir == null) {
                    String dataDirProp = System.getProperty("jboss.server.data.dir", System.getProperty("user.home"));
                    File tmp = new File(dataDirProp, "capedwarf");
                    //noinspection ResultOfMethodCallIgnored
                    tmp.mkdirs();
                    dataDir = tmp;
                }
            }
        }
        return dataDir;
    }

    protected byte[] loadBytesInternal(String key, long startIndex, long endIndex) {
        File file = new File(getDataDir(), key);
        if (file.exists() == false)
            return null;


        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            if (startIndex > 0)
                startIndex = fis.skip(startIndex);

            endIndex = endIndex - startIndex; // actual length
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while ((endIndex > 0) && ((b = fis.read()) != -1)) {
                baos.write(b);

                endIndex--;
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    protected String storeBytesInternal(String mimeType, ByteBuffer buffer) throws IOException {
        String key = UUID.randomUUID().toString();
        File file = new File(getDataDir(), key);
        FileOutputStream fos = new FileOutputStream(file);
        try {
            while (buffer.hasRemaining())
                fos.write(buffer.get());
            fos.flush();
        } finally {
            fos.close();
        }
        return key;
    }
}