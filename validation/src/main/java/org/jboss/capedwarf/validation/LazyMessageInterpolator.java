package org.jboss.capedwarf.validation;

import javax.validation.MessageInterpolator;

import java.util.Locale;

/**
 * Lazy javax.validation message interpolator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class LazyMessageInterpolator implements MessageInterpolator
{
   static MessageInterpolator INSTANCE = new LazyMessageInterpolator();
   private volatile MessageInterpolator delegate;

   protected MessageInterpolator getDelegate()
   {
      if (delegate == null)
         delegate = new SimpleMessageInterpolator();

      return delegate;
   }

   public String interpolate(String messageTemplate, Context context)
   {
      return getDelegate().interpolate(messageTemplate, context);
   }

   public String interpolate(String messageTemplate, Context context, Locale locale)
   {
      return getDelegate().interpolate(messageTemplate, context, locale);
   }
}