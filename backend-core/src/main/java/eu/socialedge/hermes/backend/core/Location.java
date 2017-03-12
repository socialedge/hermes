/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2017 SocialEdge
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
package eu.socialedge.hermes.backend.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

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
@ToString
@Getter @Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
@Embeddable @Access(AccessType.FIELD)
@NoArgsConstructor(force = true)
public class Location {
    private static final double LATITUDE_AMPLITUDE = 90;
    private static final double LONGITUDE_AMPLITUDE = 180;

    @Column(name = "latitude", nullable = false)
    private final double latitude;

    @Column(name = "longitude", nullable = false)
    private final double longitude;

    public Location(double latitude, double longitude) {
        Validate.inclusiveBetween(-LATITUDE_AMPLITUDE, LATITUDE_AMPLITUDE, latitude);
        Validate.inclusiveBetween(-LONGITUDE_AMPLITUDE, LONGITUDE_AMPLITUDE, longitude);

        this.latitude = latitude;
        this.longitude = longitude;
    }
}
