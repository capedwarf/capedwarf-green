/*
* JBoss, Home of Professional Open Source
* Copyright $today.year Red Hat Inc. and/or its affiliates and other
* contributors as indicated by the @author tags. All rights reserved.
* See the copyright.txt in the distribution for a full listing of
* individual contributors.
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

package org.jboss.lhotse.server.jee.env.impl;

import java.util.logging.Logger;
import javax.mail.Session;
import javax.transaction.TransactionManager;

/**
 * Generic environment
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class GenericEnvironment extends AbstractEnvironment
{
   private static Logger log = Logger.getLogger(GenericEnvironment.class.getName());

   /**
    * JNDI locations for TransactionManagers we know of
    */
   private static String[][] knownJNDIManagers =
         {
               {"java:/TransactionManager", "JBoss"},
               {"java:appserver/TransactionManager", "Glassfish"},
               {"javax.transaction.TransactionManager", "BEA WebLogic"},
               {"java:comp/UserTransaction", "Resin, Orion, JOnAS (JOTM)"},
         };

   public TransactionManager lookupTxManager() throws Exception
   {
      for (String[] jndiManagerName : knownJNDIManagers)
      {
         try
         {
            TransactionManager tm = doLookup(jndiManagerName[0], TransactionManager.class);
            log.info("Found Tx manager for " + jndiManagerName[1]);
            return tm;
         }
         catch (Exception ignored)
         {
         }
      }
      throw new IllegalArgumentException("No Tx manager found!");
   }

   public Session lookupMailSession()
   {
      try
      {
         return doLookup("java:/Mail", Session.class);
      }
      catch (Throwable ignored)
      {
         return null;
      }
   }
}
