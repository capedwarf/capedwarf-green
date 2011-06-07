package org.jboss.capedwarf.common.dto;

import java.io.Serializable;

/**
 * DTO model factory.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface DTOModelFactory
{
   /**
    * Create dto model.
    *
    * @param clazz the clazz
    * @return dto model
    */
   <E extends Serializable> DTOModel<E> createModel(Class<E> clazz);
}
