package org.jboss.capedwarf.common.social;

/**
 * Social event interface.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public interface SocialEvent
{
   /**
    * Get user id.
    *
    * @return the user id
    */
   Long userId();

   /**
    * Get content.
    *
    * @return the content
    */
   String content();

   /**
    * Get the parent id.
    * e.g. comment is event's child
    *
    * Can be null if no parent.
    *
    * @return parent id
    */
   Long parentId();
}
