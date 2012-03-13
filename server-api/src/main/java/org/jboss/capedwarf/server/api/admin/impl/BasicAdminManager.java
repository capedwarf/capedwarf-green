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

package org.jboss.capedwarf.server.api.admin.impl;

import org.jboss.capedwarf.server.api.admin.AdminManager;
import org.jboss.seam.solder.resourceLoader.Resource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;


/**
 * Basic admin manager.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@ApplicationScoped
public class BasicAdminManager implements AdminManager, Serializable {
    private static final long serialVersionUID = 1l;
    private Map<String, Set<String>> users = Collections.emptyMap();
    private Map<String, Set<String>> roles = Collections.emptyMap();

    public Set<String> getRoles(String identification) {
        Set<String> set = users.get(identification);
        return set != null ? set : Collections.<String>emptySet();
    }

    public boolean isUserInRole(String identification, String role) {
        Set<String> roles = getRoles(identification);
        return roles != null && roles.contains(role);
    }

    public Set<String> getUsers(String role) {
        Set<String> set = roles.get(role);
        return set != null ? set : Collections.<String>emptySet();
    }

    public String getAppAdminEmail() {
        return getSingleUser("mail");
    }

    public String getAppDevEmail() {
        return getSingleUser("dev");
    }

    /**
     * Get single user.
     *
     * @param role the role
     * @return single role user or exception if no such user
     */
    protected String getSingleUser(String role) {
        Set<String> strings = getUsers(role);
        if (strings.isEmpty())
            throw new IllegalArgumentException("No matches for " + role + ", illegal admin.properties: " + users);

        return strings.iterator().next();
    }

    @Inject
    public void setPropertes(@Resource("admin.properties") Properties props) {
        users = new HashMap<String, Set<String>>();
        roles = new HashMap<String, Set<String>>();
        for (String key : props.stringPropertyNames()) {
            String[] split = props.getProperty(key).split(",");
            users.put(key, new HashSet<String>(Arrays.asList(split)));
            for (String role : split) {
                Set<String> set = roles.get(role);
                if (set == null) {
                    set = new TreeSet<String>();
                    roles.put(role, set);
                }
                set.add(key);
            }
        }
    }
}
