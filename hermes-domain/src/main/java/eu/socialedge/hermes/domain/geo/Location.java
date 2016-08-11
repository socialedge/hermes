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

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static eu.socialedge.hermes.util.Numbers.requireBetween;

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
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode @ToString
@Embeddable
public class Location implements Serializable {

    private static final float LATITUDE_AMPLITUDE = 90;
    private static final float LONGITUDE_AMPLITUDE = 180;

    @Column(name = "location_latitude", nullable = false)
    private final float latitude;

    @Column(name = "location_longitude", nullable = false)
    private final float longitude;

    public Location(float latitude, float longitude) {
        this.latitude = requireBetween(latitude, -LATITUDE_AMPLITUDE, LATITUDE_AMPLITUDE,
                String.format("Latitude must be in range from %s to %s", -LATITUDE_AMPLITUDE, LATITUDE_AMPLITUDE));

        this.longitude = requireBetween(longitude, -LONGITUDE_AMPLITUDE, LONGITUDE_AMPLITUDE,
                String.format("Longitude must be in range from %s to %s", -LONGITUDE_AMPLITUDE, LONGITUDE_AMPLITUDE));
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
}
