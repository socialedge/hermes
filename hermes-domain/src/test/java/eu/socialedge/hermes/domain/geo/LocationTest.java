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

import org.junit.Test;

public class LocationTest {

    @Test(expected = IllegalArgumentException.class)
    public void shallNotCreateLocationIfLatitudeIsInvalid() {
        new Location(99, 100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallNotCreateLocationIfLongitudeIsInvalid() {
        new Location(90, 199);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallNotCreateLocationIfLongitudeAndLatitudeIsInvalid() {
        new Location(99, 199);
    }

    @Test
    public void shallCreateLocationIfLongitudeAndLatitudeAreEqualEdgePositiveValues() {
        new Location(90, 180);
    }

    @Test
    public void shallCreateLocationIfLongitudeAndLatitudeAreEqualEdgeNegativeValues() {
        new Location(-90, -180);
    }
}