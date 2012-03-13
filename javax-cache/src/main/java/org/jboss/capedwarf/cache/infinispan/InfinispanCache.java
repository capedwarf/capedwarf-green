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

package org.jboss.capedwarf.cache.infinispan;

import org.infinispan.container.entries.InternalCacheEntry;

import javax.cache.Cache;
import javax.cache.CacheEntry;
import javax.cache.CacheListener;
import javax.cache.CacheStatistics;
import java.util.*;

/**
 * Infinispan javax.cache wrapper.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@SuppressWarnings({"unchecked"})
class InfinispanCache implements Cache {
    private final org.infinispan.Cache cache;
    private final CacheStatistics stats;

    InfinispanCache(org.infinispan.Cache cache) {
        this.cache = cache;
        this.stats = new InfinispanCacheStatistics(cache.getAdvancedCache());
    }

    public void start() {
        cache.start();
    }

    public void stop() {
        cache.stop();
    }

    public void addListener(CacheListener cacheListener) {
        cache.addListener(new InfinispanCacheListener(cacheListener));
    }

    public void removeListener(CacheListener cacheListener) {
        cache.removeListener(new InfinispanCacheListener(cacheListener));
    }

    public void evict() {
        cache.getAdvancedCache().getEvictionManager().processEviction();
    }

    public Map getAll(Collection collection) {
        if (collection == null || collection.isEmpty())
            return Collections.emptyMap();

        Map results = new HashMap();
        for (Object key : collection)
            results.put(key, get(key));
        return results;
    }

    public CacheEntry getCacheEntry(Object key) {
        InternalCacheEntry entry = cache.getAdvancedCache().getDataContainer().get(key);
        return (entry != null) ? new InfinispanCacheEntry(entry) : null;
    }

    public CacheStatistics getCacheStatistics() {
        return stats;
    }

    public void load(Object key) {
        get(key);
    }

    public void loadAll(Collection c) {
        if (c == null || c.isEmpty())
            return;

        for (Object o : c)
            load(o);
    }

    public Object peek(Object key) {
        return get(key);
    }

    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    public Object get(Object key) {
        return cache.get(key);
    }

    public Object put(Object key, Object value) {
        return cache.put(key, value);
    }

    public Object remove(Object key) {
        return cache.remove(key);
    }

    public void putAll(Map m) {
        cache.putAll(m);
    }

    public void clear() {
        cache.clear();
    }

    public Set keySet() {
        return cache.keySet();
    }

    public Collection values() {
        return cache.values();
    }

    public Set entrySet() {
        return cache.entrySet();
    }
}
