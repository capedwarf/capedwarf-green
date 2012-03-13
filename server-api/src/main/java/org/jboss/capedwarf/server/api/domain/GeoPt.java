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

package org.jboss.capedwarf.server.api.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Geo point.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
@Embeddable
public class GeoPt implements Serializable, Comparable<GeoPt> {
    /**
     * The serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private float latitude;
    private float longitude;

    /**
     * Not to be used -- per jpa only.
     */
    @Deprecated
    public GeoPt() {
    }

    public GeoPt(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    @Deprecated
    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Deprecated
    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public boolean equals(Object obj) {
        if (obj instanceof GeoPt == false)
            return false;

        GeoPt gp = (GeoPt) obj;
        return getLatitude() == gp.getLatitude() && getLongitude() == gp.getLongitude();
    }

    public int hashCode() {
        return (int) (getLatitude() + 7 * getLongitude());
    }

    public java.lang.String toString() {
        return "GeoPt: latitude=" + getLatitude() + ", longitude=" + getLongitude();
    }

    public int compareTo(GeoPt gp) {
        float diff = getLatitude() - gp.getLatitude();
        return (int) ((diff == 0) ? (getLongitude() - gp.getLongitude()) : diff);
    }
}
