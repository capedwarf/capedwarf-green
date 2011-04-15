package org.jboss.lhotse.validation;

import java.lang.annotation.Annotation;
import java.util.regex.Pattern;

/**
 * javax.validation email validator.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractEmailConstraintValidator<T extends Annotation> extends AbstractPatternConstraintValidator<T>
{
   private static String ATOM = "[^\\x00-\\x1F^\\(^\\)^\\<^\\>^\\@^\\,^\\;^\\:^\\\\^\\\"^\\.^\\[^\\]^\\s]";
   private static String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)*";
   private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

   private static java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
         "^" + ATOM + "+(\\." + ATOM + "+)*@"
               + DOMAIN
               + "|"
               + IP_DOMAIN
               + ")$",
         java.util.regex.Pattern.CASE_INSENSITIVE
   );

   protected Pattern createPattern(T constraintAnnotation)
   {
      return pattern;
   }

   protected Boolean lengthCheck(String string)
   {
      return (string.length() == 0) ? true : null;
   }
}
