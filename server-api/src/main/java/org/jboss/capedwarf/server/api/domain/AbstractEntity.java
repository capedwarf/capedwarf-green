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

package org.jboss.capedwarf.server.api.domain;

import org.jboss.capedwarf.jpa.Entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Generic entity.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable, Entity {
    private static long serialVersionUID = 3l;
    private Long id;

    public AbstractEntity() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public String getInfo() {
        return getClass().getSimpleName() + "#" + getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass().equals(obj.getClass()) == false)
            return false;

        AbstractEntity other = (AbstractEntity) obj;
        return safeGet(id) == safeGet(other.getId());
    }

    public String toString() {
        return getInfo();
    }

    @Override
    public int hashCode() {
        return new Long(safeGet(id)).intValue();
    }

    protected static long safeGet(Long x) {
        return x == null ? 0 : x;
    }
}
