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

package org.jboss.capedwarf.server.api.io;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Decorator
public abstract class CachedBlobService extends BaseBlobService {
    private BlobService delegate;
    private BlobCache cache;

    public byte[] loadBytes(String key, long startIndex, long endIndex) {
        byte[] cached = cache.get(key);
        if (cached != null)
            return cached;

        byte[] bytes = delegate.loadBytes(key, startIndex, endIndex);
        cache.put(key, bytes);
        return bytes;
    }

    public String storeBytes(String mimeType, ByteBuffer buffer) throws IOException {
        String key = delegate.storeBytes(mimeType, buffer);
        cache.put(key, buffer.array());
        return key;
    }

    @Inject
    public void setDelegate(@Delegate BlobService delegate) {
        this.delegate = delegate;
    }

    @Inject
    public void setCache(BlobCache cache) {
        this.cache = cache;
    }
}
