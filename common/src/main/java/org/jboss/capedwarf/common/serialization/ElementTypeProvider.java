package org.jboss.capedwarf.common.serialization;

/**
 * Provide type for collection's element.
 *
 * @param <T> exact element type
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface ElementTypeProvider<T extends JSONAware>
{
   /**
    * Get element type for index.
    *
    * @param index the index
    * @return the type
    */
   Class<T> getType(int index);
}
