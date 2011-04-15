package org.jboss.lhotse.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Message template key.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Target({METHOD, TYPE})
@Retention(RUNTIME)
public @interface MessageTemplateKey
{
   /**
    * The template key.
    *
    * @return the key
    */
   String value();
}
