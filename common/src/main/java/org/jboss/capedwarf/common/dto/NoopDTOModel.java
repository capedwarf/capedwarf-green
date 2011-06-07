package org.jboss.capedwarf.common.dto;

import java.io.Serializable;

/**
 * Default DTO model.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class NoopDTOModel<U extends Serializable> implements DTOModel<U>
{
   public Object toDTO(U entity)
   {
      return entity;
   }
}
