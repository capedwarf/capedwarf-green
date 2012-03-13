/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.capedwarf.server.gae.tx;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Transaction;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.transaction.*;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@RequestScoped
@Alternative
public class GAEUserTransaction implements UserTransaction {
    protected Logger log = Logger.getLogger(GAEUserTransaction.class.getName());

    public void begin() throws NotSupportedException, SystemException {
        DatastoreService service = DatastoreServiceFactory.getDatastoreService();
        service.beginTransaction();
    }

    protected Transaction getCurrentTransaction(boolean allowNoCurrentTx) {
        DatastoreService service = DatastoreServiceFactory.getDatastoreService();
        Transaction tx = service.getCurrentTransaction();
        if (tx == null && allowNoCurrentTx == false)
            throw new IllegalStateException("No current transaction!");
        return tx;
    }

    public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException {
        getCurrentTransaction(false).commit();
    }

    public int getStatus() throws SystemException {
        Transaction tx = getCurrentTransaction(true);
        if (tx == null)
            return Status.STATUS_NO_TRANSACTION;
        else if (tx.isActive())
            return Status.STATUS_ACTIVE;
        else
            return Status.STATUS_UNKNOWN;
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        getCurrentTransaction(false).rollback();
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        log.warning("setRollbackOnly is not supported");
    }

    public void setTransactionTimeout(int i) throws SystemException {
        log.warning("setTransactionTimeout is not supported");
    }
}
