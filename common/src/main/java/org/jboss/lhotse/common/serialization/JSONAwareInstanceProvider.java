package org.jboss.lhotse.common.serialization;

/**
 * Provide instance for collection's element.
 *
 * @param <T> exact element type
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface JSONAwareInstanceProvider<T extends JSONAware>
{
   /**
    * Get element instance for index.
    *
    * @param index the index
    * @return the type
    */
    T createInstance(int index);
}
