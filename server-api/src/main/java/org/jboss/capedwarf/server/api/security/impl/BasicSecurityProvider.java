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

package org.jboss.capedwarf.server.api.security.impl;

import javax.enterprise.context.ApplicationScoped;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.jboss.capedwarf.server.api.security.SecurityProvider;

/**
 * Basic security provider.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class BasicSecurityProvider implements SecurityProvider
{
   /** The salt */
   private static final String SALT = "e20#!6J";

   public String hash(String... strings)
   {
      if (strings == null || strings.length == 0)
         throw new IllegalArgumentException("Null or empty strings: " + Arrays.toString(strings));

      try
      {
         MessageDigest m = MessageDigest.getInstance("MD5");
         StringBuilder builder = new StringBuilder();
         for (String s : strings)
            builder.append(s);
         builder.append(SALT);

         byte[] bytes = builder.toString().getBytes();
         m.update(bytes, 0, bytes.length);
         BigInteger i = new BigInteger(1, m.digest());
         return String.format("%1$032X", i);
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new IllegalArgumentException(e);
      }
   }
}
