package org.jboss.test.capedwarf.jpa.support;

import org.jboss.capedwarf.jpa.DisableProxy;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@DisableProxy
public class Person extends MockEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
