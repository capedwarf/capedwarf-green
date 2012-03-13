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

package org.jboss.capedwarf.server.api.persistence;

import java.io.Serializable;

/**
 * Stateless adapter tuple.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
final class TupleHolder {
    private static ThreadLocal<Tuple> tl = new ThreadLocal<Tuple>();

    static Tuple get() {
        Tuple tuple = tl.get();

        if (tuple != null)
            tuple.count++;

        return tuple;
    }

    static Tuple create(StatelessAdapter adapter) {
        Tuple tuple = new Tuple();
        tuple.adapter = new StatelessAdapterWrapper(adapter);
        tuple.count = 1;
        tl.set(tuple);
        return tuple;
    }

    static void close() {
        Tuple tuple = tl.get();
        if (tuple == null)
            throw new IllegalStateException("No tuple!");

        tuple.count--;
        if (tuple.count == 0) {
            tl.remove();
            tuple.adapter.doClose();
        }
    }

    static class Tuple {
        private StatelessAdapterWrapper adapter;
        private int count;

        StatelessAdapter getAdapter() {
            return adapter;
        }
    }

    private static class StatelessAdapterWrapper implements StatelessAdapter {
        private final StatelessAdapter delegate;

        private StatelessAdapterWrapper(StatelessAdapter delegate) {
            this.delegate = delegate;
        }

        void doClose() {
            delegate.close();
        }

        public void close() {
            TupleHolder.close();
        }

        public Long insert(Object entity) {
            return delegate.insert(entity);
        }

        public void update(Object entity) {
            delegate.update(entity);
        }

        public void delete(Object entity) {
            delegate.delete(entity);
        }

        public <T> T get(Class<T> entityClass, Serializable id) {
            return delegate.get(entityClass, id);
        }

        public void refresh(Object entity) {
            delegate.refresh(entity);
        }

        public void initialize(Object proxy) {
            delegate.initialize(proxy);
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return delegate.equals(obj);
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }
}
