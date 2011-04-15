package org.jboss.lhotse.validation;

import javax.validation.Path;
import javax.validation.TraversableResolver;

import java.lang.annotation.ElementType;

/**
 * Simple javax.validation traversable resolver.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class NoopTraversableResolver implements TraversableResolver
{
   static TraversableResolver INSTANCE = new NoopTraversableResolver();

   public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType)
   {
      return true;
   }

   public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType)
   {
      return false;
   }
}