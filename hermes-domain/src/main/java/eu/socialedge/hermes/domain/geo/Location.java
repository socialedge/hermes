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
package eu.socialedge.hermes.domain.geo;

import eu.socialedge.hermes.domain.ext.ValueObject;

import java.io.Serializable;
import java.util.Objects;

import static eu.socialedge.hermes.domain.shared.util.Numbers.reqBetween;

/**
 * <p>Represents a specific horizontal position in geographic coordinate system
 * with latitude and longitude.</p>
 *
 * <p>The combination of these two components specifies
 * the position of any location on the surface of the Earth, without consideration
 * of altitude or depth.</p>
 *
 * @see <a href="https://goo.gl/hB4q0K">wikipedia.org - Geographic latitude and longitude</a>
 */
@ValueObject
public class Location implements Serializable {

    private static final float LATITUDE_AMPLITUDE = 90;
    private static final float LONGITUDE_AMPLITUDE = 180;

    private final float latitude;
    private final float longitude;

    public Location(float latitude, float longitude) {
        this.latitude = reqBetween(latitude, -LATITUDE_AMPLITUDE, LATITUDE_AMPLITUDE,
                            "Latitude must be +- " + LATITUDE_AMPLITUDE);

        this.longitude = reqBetween(longitude, -LONGITUDE_AMPLITUDE, LONGITUDE_AMPLITUDE,
                            "Longtitude must be +- " + LATITUDE_AMPLITUDE);
    }

    public static Location of(float latitude, float longitude) {
        return new Location(latitude, longitude);
    }

    public float latitude() {
        return latitude;
    }

    public float longitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Objects.equals(latitude(), location.latitude()) &&
                Objects.equals(longitude(), location.longitude());
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude(), longitude());
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
