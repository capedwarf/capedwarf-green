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

package org.jboss.capedwarf.server.api.mail.impl;

import org.jboss.capedwarf.server.api.admin.AdminManager;
import org.jboss.capedwarf.server.api.mail.MailManager;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Abstract mail manager.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public abstract class AbstractMailManager implements MailManager
{
   protected Logger log = Logger.getLogger(AbstractMailManager.class.getName());
   protected AdminManager adminManager;

   public void sendEmailFromAdmin(String subject, String textBody, String... tos)
   {
      sendEmail(adminManager.getAppAdminEmail(), subject, textBody, tos);
   }

   public void sendEmailToAdmins(String subject, String textBody)
   {
      sendEmailToAdmins(adminManager.getAppAdminEmail(), subject, textBody);
   }

   @Inject
   public void setAdminManager(AdminManager adminManager)
   {
      this.adminManager = adminManager;
   }
}
