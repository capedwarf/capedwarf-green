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

import javax.transaction.*;
import javax.transaction.xa.XAResource;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UserTransaction based Transaction.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 * @author Bela Ban
 */
class UTTransaction extends UserTransactionAdapter implements Transaction {
    static Transaction INSTANCE = new UTTransaction();

    private static final Logger log = Logger.getLogger(UTTransaction.class.getName());
    private static boolean trace = log.isLoggable(Level.FINEST);

    private static ThreadLocal<Set<Synchronization>> tlSyncs = new ThreadLocal<Set<Synchronization>>();

    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException {
        try {
            if (notifyBeforeCompletion()) {
                try {
                    getUserTransaction().commit();
                } finally {
                    notifyAfterCompletion(getStatus());
                }
            } else {
                try {
                    getUserTransaction().rollback();
                } finally {
                    notifyAfterCompletion(Status.STATUS_ROLLEDBACK);
                }
            }
        } finally {
            cleanup();
        }
    }

    public void rollback() throws IllegalStateException, SystemException {
        try {
            getUserTransaction().rollback();
            notifyAfterCompletion(Status.STATUS_ROLLEDBACK);
        } finally {
            cleanup();
        }
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        getUserTransaction().setRollbackOnly();
    }

    public int getStatus() throws SystemException {
        return getUserTransaction().getStatus();
    }

    public boolean enlistResource(XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        return false; // TODO -- anyway to get Tx or at least Xid
    }

    public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException {
        return false;
    }

    public void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
        if (sync == null)
            throw new IllegalArgumentException("null synchronization " + this);

        int status = getStatus();
        switch (status) {
            case Status.STATUS_ACTIVE:
            case Status.STATUS_PREPARING:
                break;
            case Status.STATUS_PREPARED:
                throw new IllegalStateException("already prepared. " + this);
            case Status.STATUS_COMMITTING:
                throw new IllegalStateException("already started committing. " + this);
            case Status.STATUS_COMMITTED:
                throw new IllegalStateException("already committed. " + this);
            case Status.STATUS_MARKED_ROLLBACK:
                throw new RollbackException("already marked for rollback " + this);
            case Status.STATUS_ROLLING_BACK:
                throw new RollbackException("already started rolling back. " + this);
            case Status.STATUS_ROLLEDBACK:
                throw new RollbackException("already rolled back. " + this);
            case Status.STATUS_NO_TRANSACTION:
                throw new IllegalStateException("no transaction. " + this);
            case Status.STATUS_UNKNOWN:
                throw new IllegalStateException("unknown state " + this);
            default:
                throw new IllegalStateException("illegal status: " + status + " tx=" + this);
        }

        if (trace)
            log.finest("registering synchronization handler " + sync);

        Set<Synchronization> syncs = tlSyncs.get();
        if (syncs == null) {
            syncs = new HashSet<Synchronization>(8);
            tlSyncs.set(syncs);
        }
        syncs.add(sync);
    }

    protected boolean notifyBeforeCompletion() throws SystemException {
        Set<Synchronization> syncs = tlSyncs.get();
        if (syncs == null)
            return true;

        boolean retval = true;
        for (Synchronization s : syncs) {
            if (trace)
                log.finest("processing beforeCompletion for " + s);

            try {
                s.beforeCompletion();
            } catch (Throwable t) {
                retval = false;
                log.log(Level.SEVERE, "beforeCompletion() failed for " + s, t);
            }
        }
        return retval;
    }

    protected void notifyAfterCompletion(int status) {
        Set<Synchronization> syncs = tlSyncs.get();
        if (syncs == null)
            return;

        try {
            for (Synchronization s : syncs) {
                if (trace)
                    log.finest("processing afterCompletion for " + s);

                try {
                    s.afterCompletion(status);
                } catch (Throwable t) {
                    log.log(Level.SEVERE, "afterCompletion() failed for " + s, t);
                }
            }
            syncs.clear();
        } finally {
            tlSyncs.remove();
        }
    }
}
