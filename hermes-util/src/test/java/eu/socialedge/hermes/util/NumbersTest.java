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
package eu.socialedge.hermes.util;

import org.junit.Test;

import static eu.socialedge.hermes.util.Numbers.requireBetween;
import static eu.socialedge.hermes.util.Numbers.requireExclusiveBetween;

public class NumbersTest {

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionIfExceedsUpperBound() {
        requireBetween(10, 9, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionIfExceedsLowerBound() {
        requireBetween(15, 16, 20);
    }

    @Test
    public void shallBeInclusive() {
        requireBetween(20, 20, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionIfEqualsUpperBound() {
        requireExclusiveBetween(10, 10, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionIfEqualsLowerBound() {
        requireExclusiveBetween(15, 15, 20);
    }
}