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

package org.jboss.capedwarf.server.api.dao.impl;

import org.jboss.capedwarf.server.api.cache.EntityListCachedResult;
import org.jboss.capedwarf.server.api.domain.TimestampedEntity;

import java.util.Collections;
import java.util.List;

/**
 * Timestamped list cache result.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class TimestampedListCachedResult extends EntityListCachedResult {
    private static final long serialVersionUID = 1l;

    private long timestamp;
    private long[] timestamps;

    public TimestampedListCachedResult(long timestamp, List<? extends TimestampedEntity> results) {
        super(toIds(results));
        this.timestamp = timestamp;
        if (results != null && results.isEmpty() == false) {
            this.timestamps = new long[results.size()];
            int i = 0;
            for (TimestampedEntity te : results)
                timestamps[i++] = te.getTimestamp();
        } else {
            timestamps = new long[0];
        }
    }

    /**
     * Get proper entity id sublist based on timestamp.
     *
     * @param ts the timestamp
     * @return id sublist
     */
    List<Long> getSubList(long ts) {
        if (ts < timestamp)
            return null; // we need to get new timestamps

        int size = timestamps.length;

        if (size == 0)
            return Collections.emptyList();

        long lastTs = timestamps[size - 1];
        if (ts > lastTs)
            return Collections.emptyList();

        for (int i = 0; i < size; i++) {
            Long te = timestamps[i];
            if (te > ts)
                return getIds().subList(i, size);
        }

        return Collections.emptyList();
    }
}

