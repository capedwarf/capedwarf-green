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

package org.jboss.capedwarf.server.gae.cache;

import org.datanucleus.cache.CachedPC;
import org.jboss.capedwarf.server.api.cache.impl.AbstractCacheEntryLookup;

/**
 * DataNucleus CEL.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class DNCacheEntryLookup extends AbstractCacheEntryLookup {
    @Override
    protected <T> T toEntity(Class<T> entryType, Object oid, Object result) {
        CachedPC cpc = (CachedPC) result;

        // Check if we are fully loaded
        int countLoadedFileds = 0;
        for (boolean lf : cpc.getLoadedFields())
            if (lf) countLoadedFileds++;
        // We only have id loaded (best guess if we're loaded)
        if (countLoadedFileds <= 1)
            return null;

        // Create active version of cached object with ObjectProvider connected and same id
        /*
        ExecutionContext ec = null;
        ObjectProvider op = ObjectProviderFactory.newForCachedPC(ec, oid, cpc);
        return entryType.cast(op.getObject());
        */
        return null; // TODO
    }
}
