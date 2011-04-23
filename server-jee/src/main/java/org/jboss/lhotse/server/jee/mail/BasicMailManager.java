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

package org.jboss.lhotse.server.jee.mail;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.lhotse.server.api.mail.impl.AbstractMailManager;
import org.jboss.lhotse.server.jee.env.Environment;

/**
 * JEE mail manager impl.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class BasicMailManager extends AbstractMailManager
{
   private Session session;

   public void sendEmail(String sender, String subject, String textBody, String... tos)
   {
      if (session == null)
         return;

      try
      {
         Address[] addresses = new Address[tos.length];
         for (int i = 0; i < tos.length; i++)
            addresses[i] = new InternetAddress(tos[i]);

         Message msg = new MimeMessage(session);
         msg.setFrom(new InternetAddress(sender));
         msg.addRecipients(Message.RecipientType.TO, addresses);
         msg.setSubject(subject);
         msg.setText(textBody);
         Transport.send(msg);
      }
      catch (Exception e)
      {
         log.warning("Failed to send email: " + e);
      }
   }

   public void sendEmailToAdmins(String sender, String subject, String textBody)
   {
      sendEmail(sender, subject, textBody, adminManager.getAppAdminEmail());
   }

   @Inject
   public void setEnv(Environment env) throws Exception
   {
      session = env.lookupMailSession();
      if (session == null)
         log.warning("No mail session setup.");
   }
}
