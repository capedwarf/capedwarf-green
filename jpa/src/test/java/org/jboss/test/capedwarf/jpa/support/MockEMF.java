package org.jboss.test.capedwarf.jpa.support;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Map;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MockEMF implements EntityManagerFactory {
    private EntityManager em;

    public EntityManager createEntityManager() {
        if (em == null)
            em = new MockEM();

        return em;
    }

    public EntityManager createEntityManager(Map map) {
        return createEntityManager();
    }

    public void close() {
    }

    public boolean isOpen() {
        return false;
    }
}
