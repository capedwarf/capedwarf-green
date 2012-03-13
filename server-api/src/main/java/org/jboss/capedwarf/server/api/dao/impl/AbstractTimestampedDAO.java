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

import org.jboss.capedwarf.server.api.dao.TimestampedDAO;
import org.jboss.capedwarf.server.api.domain.TimestampedEntity;
import org.jboss.capedwarf.server.api.tx.TransactionPropagationType;
import org.jboss.capedwarf.server.api.tx.Transactional;
import org.jboss.capedwarf.server.api.utils.TimestampProvider;

import javax.inject.Inject;

/**
 * Abstract timestamped DAO.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractTimestampedDAO<T extends TimestampedEntity> extends AbstractGenericDAO<T> implements TimestampedDAO<T> {
    private TimestampProvider tp;

    /**
     * Get current timestamp.
     *
     * @return the timestamp
     */
    protected long currentTimestamp() {
        return tp.currentTimeMillis();
    }

    protected void saveInternal(T entity) {
        long ts = tp.currentTimeMillis();
        entity.setTimestamp(ts);
        entity.setExpirationTime(entity.getExpirationTime() + ts); // current ts is actually delta
        super.saveInternal(entity);
    }

    @Transactional(TransactionPropagationType.SUPPORTS)
    public boolean hasExpired(T entity) {
        if (entity == null)
            return true;

        long expirationTime = entity.getExpirationTime();
        return expirationTime > tp.currentTimeMillis();
    }

    @Inject
    public void setTp(TimestampProvider tp) {
        this.tp = tp;
    }
}
