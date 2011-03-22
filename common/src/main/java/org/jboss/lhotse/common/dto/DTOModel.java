package org.jboss.lhotse.common.dto;

import java.io.Serializable;

/**
 * DTO model.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface DTOModel<E extends Serializable>
{
   /**
    * To DTO.
    *
    * @param entity the entity
    * @return dto object
    */
   Object toDTO(E entity);
}
