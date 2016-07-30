/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class JpaLocation implements Serializable {

    @Column(name = "location_latitude", nullable = false)
    private float latitude;

    @Column(name = "location_longitude", nullable = false)
    private float longitude;

    public JpaLocation() {}

    public float latitude() {
        return latitude;
    }

    public void latitude(float latitude) {
        this.latitude = latitude;
    }

    public float longitude() {
        return longitude;
    }

    public void longitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaLocation)) return false;
        JpaLocation that = (JpaLocation) o;
        return Float.compare(that.latitude, latitude) == 0 && Float.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
