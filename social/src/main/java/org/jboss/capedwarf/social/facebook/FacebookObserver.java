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

package org.jboss.capedwarf.social.facebook;

import org.jboss.capedwarf.common.io.URLAdapter;
import org.jboss.capedwarf.common.serialization.JSONSerializator;
import org.jboss.capedwarf.common.social.SocialEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Facebook observer.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class FacebookObserver
{
   private static final Logger log = Logger.getLogger(FacebookObserver.class.getName());

   static final String OBJECT_URL = "https://graph.facebook.com/%1$1s";
   static final String CONNECTION_URL = OBJECT_URL + "/%2$1s?access_token=%3$1s&message=%4$1s";

   static final String CURRENT_USER = "me";
   static final String FEED = "feed";
   static final String COMMENTS = "comments";

   private URLAdapter urlAdapter;

   /**
    * Handle social event.
    *
    * @param event the social event
    * @return post id or null if no post has been made
    */
   public String publish(@Observes(during = TransactionPhase.AFTER_SUCCESS) SocialEvent event)
   {
      Long userId = event.userId();
      String accessToken = readAccessToken(userId);
      if (accessToken != null)
      {
         List<String> args = new ArrayList<String>();
         Long parentId = event.parentId();
         if (parentId == null)
         {
            args.addAll(Arrays.asList(CURRENT_USER, FEED));
         }
         else
         {
            String postId = readPostId(userId, parentId);
            if (postId != null)
            {
               args.addAll(Arrays.asList(postId, COMMENTS));
            }
         }
         if (args.size() > 0)
         {
            try
            {
               args.add(URLEncoder.encode(accessToken, "UTF-8"));
               args.add(URLEncoder.encode(event.content(), "UTF-8"));
               String query = new Formatter().format(CONNECTION_URL, args.toArray()).toString();
               URL url = new URL(query);
               InputStream is = urlAdapter.fetch(url);
               PostId postId = JSONSerializator.INSTANCE.deserialize(is, PostId.class);
               return postId.getId();
            }
            catch (IOException e)
            {
               log.log(Level.WARNING, "Unable to publish social event: " + event, e);
            }
         }
      }
      return null;
   }

   /**
    * Read the access token for client / user.
    *
    * @param userId the user id.
    * @return access token or null if no such token
    */
   protected String readAccessToken(Long userId)
   {
      return "";
   }

   /**
    * Read post id.
    *
    * @param userId the user id
    * @param parentId the parent id
    * @return previous post id
    */
   protected String readPostId(Long userId, Long parentId)
   {
      return null;
   }

   @Inject
   public void setUrlAdapter(URLAdapter urlAdapter)
   {
      this.urlAdapter = urlAdapter;
   }
}
