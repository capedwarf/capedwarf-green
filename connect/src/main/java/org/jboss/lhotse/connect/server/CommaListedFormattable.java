package org.jboss.lhotse.connect.server;

import java.util.Formattable;
import java.util.Formatter;

/**
 * Comma listed formattable
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
class CommaListedFormattable implements Formattable
{
   private Iterable iterable;

   CommaListedFormattable(Iterable iterable)
   {
      if (iterable == null)
         throw new IllegalArgumentException("Null iterable");
      this.iterable = iterable;
   }

   public void formatTo(Formatter formatter, int flags, int width, int precision)
   {
      StringBuilder builder = new StringBuilder();
      for (Object arg : iterable)
      {
         if (builder.length() > 0)
            builder.append(",");
         builder.append(arg);
      }
      formatter.format(builder.toString());
   }
}