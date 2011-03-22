package org.jboss.lhotse.common.serialization;

/**
 * Delegate serializator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class DelegateSerializator extends AbstractSerializator
{
   protected Serializator delegate;

   public DelegateSerializator(Serializator delegate)
   {
      if (delegate == null)
         throw new IllegalArgumentException("Null delegate");
      this.delegate = delegate;
   }

   public boolean isValid(Class<?> clazz)
   {
      return delegate.isValid(clazz);
   }
}
