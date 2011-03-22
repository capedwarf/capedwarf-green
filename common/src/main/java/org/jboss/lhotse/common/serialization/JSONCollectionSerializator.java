package org.jboss.lhotse.common.serialization;

/**
 * JSON collection based serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JSONCollectionSerializator extends MultiJSONCollectionSerializator
{
   @SuppressWarnings({"unchecked"})
   public JSONCollectionSerializator(final Class<? extends JSONAware> clazz)
   {
      super(new ReflectionJSONAwareInstanceProvider(new ElementTypeProvider()
      {
         public Class getType(int index)
         {
            return clazz;
         }
      }));
   }
}
