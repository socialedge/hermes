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

import static eu.socialedge.hermes.util.Values.requireNotNull;
import static org.junit.Assert.assertEquals;

public class ValuesTest {

    @Test
    public void shallNotThrowExceptionIfObjIsNotNull() {
        requireNotNull("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionIfObjIsNull() {
        requireNotNull(null);
    }

    @Test
    public void shallReturnDefaultValueIfObjIsNull() {
        Object defaultValue = "";
        Object requiredResult = requireNotNull(null, defaultValue);

        assertEquals(defaultValue, requiredResult);
    }
}