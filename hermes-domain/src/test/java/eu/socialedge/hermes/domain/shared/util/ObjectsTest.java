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
package eu.socialedge.hermes.domain.shared.util;

import org.junit.Test;

import static eu.socialedge.hermes.domain.shared.util.Objects.reqNotNull;

public class ObjectsTest {

    @Test
    public void shallNotThrowExceptionIfObjIsNotNull() {
        reqNotNull("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionIfObjIsNull() {
        reqNotNull(null);
    }
}