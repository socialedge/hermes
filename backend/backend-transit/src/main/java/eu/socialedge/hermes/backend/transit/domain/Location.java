/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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
package eu.socialedge.hermes.backend.transit.domain;

import lombok.*;
import org.apache.commons.lang3.Validate;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * <p>Represents a specific horizontal position in geographic coordinate system
 * with latitude and longitude.</p>
 *
 * <p>The combination of these two components specifies
 * the position of any location on the surface of the Earth, without consideration
 * of altitude or depth.</p>
 *
 * @see <a href="https://goo.gl/hB4q0K">
 *     wikipedia.org - Geographic latitude and longitude</a>
 */
@Document
@ToString @EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Location {
    private static final double LATITUDE_AMPLITUDE = 90;
    private static final double LONGITUDE_AMPLITUDE = 180;

    @Getter
    private final double latitude;

    @Getter
    private final double longitude;

    public Location(double latitude, double longitude) {
        Validate.inclusiveBetween(-LATITUDE_AMPLITUDE, LATITUDE_AMPLITUDE, latitude);
        Validate.inclusiveBetween(-LONGITUDE_AMPLITUDE, LONGITUDE_AMPLITUDE, longitude);

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Location of(double latitude, double longitude) {
        return new Location(latitude, longitude);
    }
}
