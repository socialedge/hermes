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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PositionTest {

    @Test
    public void testNormalValues() {
        new Position(14, 14);
    }

    @Test
    public void testOf() {
        Position conctructor = new Position(14, 14);
        Position of = Position.of(14, 14);
        assertEquals(conctructor, of);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatitudeHigherThen90() {
        new Position(91, 12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLatitudeLessThenMinus90() {
        new Position(-91, 12);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testLongitudeHigherThen180() {
        new Position(15, 181);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLongitudeLessThenMinus180() {
        new Position(15, -181);
    }
}
