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

package org.jboss.test.lhotse.social.facebook.test;

import org.jboss.lhotse.social.facebook.FacebookObserver;
import org.jboss.test.lhotse.social.facebook.support.TestSocialEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class FBTestCase
{
   protected FacebookObserver getFacebookObserver(final String postId)
   {
      return new FacebookObserver()
      {
         protected String readAccessToken(Long userId)
         {
            return "1234567890"; // TODO
         }

         protected String readPostId(Long userId, Long parentId)
         {
            return postId;
         }
      };
   }

   @Before
   public void setUp()
   {
      // TODO
   }

   @After
   public void tearDown()
   {
      // TODO
   }

   @Test
   public void testUpdateStatus() throws Exception
   {
      FacebookObserver fbObserver = getFacebookObserver(null);
      fbObserver.publish(new TestSocialEvent("Test-" + System.currentTimeMillis()));
   }

   @Test
   public void testAddComment() throws Exception
   {
      // event
      FacebookObserver fbObserver = getFacebookObserver(null);
      String postId = fbObserver.publish(new TestSocialEvent("Test-" + System.currentTimeMillis()));
      // comment
      fbObserver = getFacebookObserver(postId);
      TestSocialEvent event = new TestSocialEvent("Test-" + System.currentTimeMillis());
      event.setParentId(1L);
      fbObserver.publish(event);
   }
}
