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

package org.jboss.capedwarf.server.api.cache;

import org.jboss.capedwarf.jpa.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cache only entity ids.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class EntityListCachedResult implements Serializable {
    private static final long serialVersionUID = 1l;
    private List<Long> ids;

    protected static List<Long> toIds(List<? extends Entity> entities) {
        if (entities == null || entities.isEmpty())
            return Collections.emptyList();

        List<Long> ids = new ArrayList<Long>();
        for (Entity e : entities)
            ids.add(e.getId());
        return ids;
    }

    protected EntityListCachedResult(List<Long> ids) {
        if (ids == null)
            ids = Collections.emptyList();
        this.ids = ids;
    }

    public List<Long> getIds() {
        return ids;
    }
}