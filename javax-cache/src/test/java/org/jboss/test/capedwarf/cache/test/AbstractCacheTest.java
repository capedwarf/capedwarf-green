/*
* JBoss, Home of Professional Open Source
* Copyright $today.year Red Hat Inc. and/or its affiliates and other
* contributors as indicated by the @author tags. All rights reserved.
* See the copyright.txt in the distribution for a full listing of
* individual contributors.
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

package org.jboss.test.capedwarf.cache.test;

import org.junit.Assert;

import javax.cache.Cache;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import java.util.Collections;
import java.util.Map;

/**
 * Setup Infinispan cache.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractCacheTest {
    protected CacheManager getCacheManager() {
        CacheManager manager = CacheManager.getInstance();
        Assert.assertNotNull(manager);
        return manager;
    }

    protected CacheFactory getCacheFactory() throws Throwable {
        CacheFactory factory = getCacheManager().getCacheFactory();
        Assert.assertNotNull(factory);
        return factory;
    }

    protected Cache getCache(String name) throws Throwable {
        Map map = Collections.singletonMap("cache-name", name);
        Cache cache = getCacheFactory().createCache(map);
        Assert.assertNotNull(cache);
        return cache;
    }
}
