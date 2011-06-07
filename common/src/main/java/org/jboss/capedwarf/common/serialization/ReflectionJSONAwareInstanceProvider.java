package org.jboss.capedwarf.common.serialization;

/**
 * Provide instance for collection's element.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ReflectionJSONAwareInstanceProvider<T extends JSONAware> implements JSONAwareInstanceProvider<T>
{
   private ElementTypeProvider<T> elementTypeProvider;

   public ReflectionJSONAwareInstanceProvider(ElementTypeProvider<T> elementTypeProvider)
   {
      if (elementTypeProvider == null)
         throw new IllegalArgumentException("Null element type provider");

      this.elementTypeProvider = elementTypeProvider;
   }

   public T createInstance(int index)
   {
      try
      {
         Class<T> clazz = elementTypeProvider.getType(index);
         return clazz.newInstance();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
