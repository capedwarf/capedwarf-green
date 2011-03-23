/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.test.lhotse.social.facebook.support;

import org.jboss.lhotse.common.social.SocialEvent;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class TestSocialEvent implements SocialEvent
{
   private Long userId;
   private String content;
   private Long parentId;

   public TestSocialEvent(String content)
   {
      this.content = content;
   }

   public Long userId()
   {
      return getUserId();
   }

   public String content()
   {
      return getContent();
   }

   public Long parentId()
   {
      return getParentId();
   }

   public Long getUserId()
   {
      return userId;
   }

   public void setUserId(Long userId)
   {
      this.userId = userId;
   }

   public String getContent()
   {
      return content;
   }

   public void setContent(String content)
   {
      this.content = content;
   }

   public Long getParentId()
   {
      return parentId;
   }

   public void setParentId(Long parentId)
   {
      this.parentId = parentId;
   }
}
