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

package org.jboss.test.lhotse.cache.test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.cache.Cache;
import javax.cache.CacheEntry;
import javax.cache.CacheStatistics;

import org.jboss.test.lhotse.cache.support.TestListener;
import org.junit.Assert;
import org.junit.Test;

/**
 * Smoke tests.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class SmokeTestCase extends AbstractCacheTest
{
   @Test
   public void testCache() throws Throwable
   {
      Cache cache = getCache("TestCache");

      Object key = "SomeKey";
      Object value = "SomeValue";

      cache.put(key, value);
      Assert.assertEquals(value, cache.get(key));
      Assert.assertEquals(value, cache.peek(key));
      Assert.assertTrue(cache.containsKey(key));
      // Assert.assertTrue(cache.containsValue(value));

      Assert.assertEquals(value, cache.remove(key));
      Assert.assertNull(cache.get(key));

      cache.put(key, value);
      Assert.assertFalse(cache.isEmpty());
      Assert.assertEquals(1, cache.size());

      cache.clear();
      Assert.assertTrue(cache.isEmpty());

      cache.putAll(Collections.singletonMap(key, value));
      Set keySet = cache.keySet();
      Assert.assertNotNull(keySet);
      Assert.assertEquals(key, keySet.iterator().next());
      Set entrySet = cache.entrySet();
      Assert.assertNotNull(entrySet);
      Assert.assertEquals(1, entrySet.size());
      Map.Entry entry = (Map.Entry) entrySet.iterator().next();
      Assert.assertEquals(key, entry.getKey());
      Assert.assertEquals(value, entry.getValue());

      cache.load(key);
      cache.loadAll(Collections.singleton(key));

      Map all = cache.getAll(Collections.singleton(key));
      Assert.assertNotNull(all);
      Assert.assertEquals(value, all.get(key));
   }

   @Test
   public void testListener() throws Throwable
   {
      Cache cache = getCache("TestCache");
      TestListener listener = new TestListener();
      cache.addListener(listener);

      Object key = "SomeKey";
      Object value = "SomeValue";

      cache.put(key, value);
      Assert.assertEquals(key, listener.target);

      cache.remove(key);
      Assert.assertNull(listener.target);
      Assert.assertEquals("Remove", listener.state);

      cache.put(key, value);
      Assert.assertEquals(key, listener.target);

      cache.clear();
      Assert.assertNull(listener.target);
      Assert.assertEquals("Remove", listener.state); // TODO -- no clear callback?

      cache.put(key, value);
      Assert.assertEquals(key, listener.target);

      cache.evict();
      // TODO -- actual evict events?
      // Assert.assertNull(listener.target);
      // Assert.assertEquals("Evict", listener.state);
   }

   @Test
   public void testStats() throws Throwable
   {
      Cache cache = getCache("StatCache");

      Object key = "SomeKey";
      Object value = "SomeValue";

      CacheStatistics stats = cache.getCacheStatistics();
      stats.clearStatistics();

      cache.put(key, value);
      Assert.assertEquals(1, stats.getObjectCount());

      cache.get(key);
      Assert.assertEquals(1, stats.getCacheHits());
      cache.get("WrongKey");
      Assert.assertEquals(1, stats.getCacheMisses());
   }

   @Test
   public void testEntry() throws Throwable
   {
      Cache cache = getCache("TestCache");

      Object key = "SomeKey";
      Object value = "SomeValue";

      cache.put(key, value);

      long ts = System.currentTimeMillis();
      cache.get(key);
      CacheEntry entry = cache.getCacheEntry(key);
      Assert.assertNotNull(entry);
      Assert.assertEquals(key, entry.getKey());
      Assert.assertEquals(value, entry.getValue());
      // Assert.assertTrue(entry.isValid()); // TODO -- not valid?
      long lastAccessTime = entry.getLastAccessTime();
      // Assert.assertTrue(lastAccessTime >= ts); // TODO -- getting -1?
   }
}
