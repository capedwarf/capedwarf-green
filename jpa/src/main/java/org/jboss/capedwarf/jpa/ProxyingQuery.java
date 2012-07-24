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

package org.jboss.capedwarf.jpa;

import javax.persistence.FlushModeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Proxy wrapping Query.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ProxyingQuery extends ProxyingHelper implements Query {
    private EntityManagerProvider provider;
    private Query delegate;

    protected ProxyingQuery(EntityManagerProvider provider, Query delegate) {
        if (provider == null)
            throw new IllegalArgumentException("Null provider");
        if (delegate == null)
            throw new IllegalArgumentException("Null delegate");

        this.provider = provider;
        this.delegate = delegate;
    }

    protected EntityManagerProvider getProvider() {
        return provider;
    }

    @SuppressWarnings({"unchecked"})
    public List getResultList() {
        List result = delegate.getResultList();
        if (result == null)
            return null;

        List list = new ArrayList();
        for (Object entity : result)
            list.add(wrap(entity));

        return list;
    }

    public Object getSingleResult() {
        Object entity = delegate.getSingleResult();
        return wrap(entity);
    }

    public int executeUpdate() {
        return delegate.executeUpdate();
    }

    public Query setMaxResults(int i) {
        return delegate.setMaxResults(i);
    }

    public Query setFirstResult(int i) {
        return delegate.setFirstResult(i);
    }

    public Query setFlushMode(FlushModeType flushModeType) {
        return delegate.setFlushMode(flushModeType);
    }

    public Query setHint(String s, Object o) {
        return delegate.setHint(s, o);
    }

    public Query setParameter(String s, Object o) {
        return delegate.setParameter(s, o);
    }

    public Query setParameter(String s, Date date, TemporalType temporalType) {
        return delegate.setParameter(s, date, temporalType);
    }

    public Query setParameter(String s, Calendar calendar, TemporalType temporalType) {
        return delegate.setParameter(s, calendar, temporalType);
    }

    public Query setParameter(int i, Object o) {
        return delegate.setParameter(i, o);
    }

    public Query setParameter(int i, Date date, TemporalType temporalType) {
        return delegate.setParameter(i, date, temporalType);
    }

    public Query setParameter(int i, Calendar calendar, TemporalType temporalType) {
        return delegate.setParameter(i, calendar, temporalType);
    }
}
