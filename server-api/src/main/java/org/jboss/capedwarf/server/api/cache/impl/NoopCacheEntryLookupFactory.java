package org.jboss.capedwarf.server.api.cache.impl;

import org.jboss.capedwarf.server.api.cache.CacheEntryLookup;

import javax.cache.Cache;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 * Noop CELF.
 *
 * Most cached items are too heavy to extract.
 * e.g. Hibernate has a lot of impl details,
 * where DataNucleus just changes how entries are cached - broken to primitives.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
@Alternative
public class NoopCacheEntryLookupFactory extends AbstractCacheEntryLookupFactory {

    private static CacheEntryLookup INSTANCE = new NoopCacheEntryLookup();

    protected CacheEntryLookup doCreateCacheEntryLookup(Cache cache) {
        return INSTANCE;
    }

    private static class NoopCacheEntryLookup implements CacheEntryLookup {
        public <T> T getCachedEntry(Class<T> entryType, Object id) {
            return null;
        }
    }
}
