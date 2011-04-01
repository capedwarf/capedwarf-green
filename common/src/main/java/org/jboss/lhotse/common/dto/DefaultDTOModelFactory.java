package org.jboss.lhotse.common.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Default DTO model factory.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class DefaultDTOModelFactory implements DTOModelFactory
{
   private Map<Class<? extends Serializable>, DTOModel<? extends Serializable>> models = new WeakHashMap<Class<? extends Serializable>, DTOModel<? extends Serializable>>();

   @SuppressWarnings({"unchecked"})
   public <E extends Serializable> DTOModel<E> createModel(Class<E> clazz)
   {
      DTOModel model = models.get(clazz);
      if (model != null)
         return model;

      model = createModelInternal(clazz);
      models.put(clazz, model);

      return model;
   }

   protected <E extends Serializable> DTOModel createModelInternal(Class<E> clazz)
   {
      DTOClass dtoClass = clazz.getAnnotation(DTOClass.class);
      if (dtoClass == null)
         throw new IllegalArgumentException("No such DTOClass: " + clazz);

      Class<? extends DTOModel> modelClass = dtoClass.model();
      if (modelClass != DefaultDTOModel.class)
      {
         try
         {
            return modelClass.newInstance();
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         if (clazz == dtoClass.value())
            return new NoopDTOModel<E>();
         else
            return new DefaultDTOModel<E>(clazz);
      }
   }
}
