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

package org.jboss.capedwarf.server.gae.mail;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import org.jboss.capedwarf.server.api.mail.impl.AbstractMailManager;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic mail manager.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class BasicMailManager extends AbstractMailManager {
    private Logger log = Logger.getLogger(BasicMailManager.class.getName());

    private MailService mailService = MailServiceFactory.getMailService();

    public void sendEmail(String sender, String subject, String textBody, String... tos) {
        for (String to : tos) {
            try {
                MailService.Message msg = new MailService.Message(sender, to, subject, textBody);
                mailService.send(msg);
            } catch (Throwable t) {
                log.warning("Failed to send email: " + t);
            }
        }
    }

    public void sendEmailToAdmins(String sender, String subject, String textBody) {
        try {
            MailService.Message msg = new MailService.Message();
            msg.setSender(sender);
            msg.setSubject(subject);
            msg.setTextBody(textBody);
            mailService.sendToAdmins(msg);
        } catch (Throwable t) {
            String info = Arrays.asList(sender, subject, textBody).toString();
            log.log(Level.WARNING, "Failed to send email: " + info, t);
        }
    }
}
