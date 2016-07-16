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
package eu.socialedge.hermes.domain.infrastructure;

import eu.socialedge.hermes.domain.ext.ValueObject;
import org.apache.commons.lang3.Validate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public class Position implements Serializable {
    private static final int LATITUDE_AMPLITUDE = 90;
    private static final int LONGITUDE_AMPLITUDE = 180;

    @Column(name = "latitude")
    private Float latitude;

    @Column(name = "longitude")
    private Float longitude;

    protected Position() {}

    public Position(float latitude, float longitude) {
        Validate.inclusiveBetween(-LATITUDE_AMPLITUDE, LATITUDE_AMPLITUDE, latitude,
                "Latitude must be +- " + LATITUDE_AMPLITUDE);
        Validate.inclusiveBetween(-LONGITUDE_AMPLITUDE, LONGITUDE_AMPLITUDE, longitude,
                "Longtitude must be +- " + LATITUDE_AMPLITUDE);

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Position of(float latitude, float longitude) {
        return new Position(latitude, longitude);
    }

    public Float getLatitude() {
        return latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return Objects.equals(getLatitude(), position.getLatitude()) &&
                Objects.equals(getLongitude(), position.getLongitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLatitude(), getLongitude());
    }

    @Override
    public String toString() {
        return "Position{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
