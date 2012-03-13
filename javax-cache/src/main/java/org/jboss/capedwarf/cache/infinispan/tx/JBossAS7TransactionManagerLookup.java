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

package org.jboss.capedwarf.cache.infinispan.tx;

import org.infinispan.transaction.lookup.TransactionManagerLookup;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

/**
 * JBossAS7 TM lookup.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class JBossAS7TransactionManagerLookup implements TransactionManagerLookup {
    public TransactionManager getTransactionManager() throws Exception {
        Context context = new InitialContext();
        try {
            Object lookup = context.lookup("java:jboss/TransactionManager");
            return TransactionManager.class.cast(lookup);
        } finally {
            context.close();
        }
    }
}
