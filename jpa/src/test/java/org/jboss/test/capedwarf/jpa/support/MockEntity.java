package org.jboss.test.capedwarf.jpa.support;

import org.jboss.capedwarf.jpa.Entity;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MockEntity implements Entity {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
