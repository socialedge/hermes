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

import java.time.LocalDate;
import java.util.Arrays;

import static eu.socialedge.hermes.util.Strings.isBlank;
import static eu.socialedge.hermes.util.Strings.requireLongerThan;
import static eu.socialedge.hermes.util.Strings.requireNotBlank;
import static eu.socialedge.hermes.util.Strings.requireShorterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringsTest {
    @Test
    public void shallCreateValidCsvFromStringsStream() throws Exception {
        String[] strings = {"a", "b,", "c"};

        String joinedString = Strings.join(Arrays.stream(strings));

        assertEquals("a,b,,c", joinedString);
    }

    @Test
    public void shallCreateValidCsvFromArrayOfObjects() throws Exception {
        LocalDate dateNow = LocalDate.now();

        Object[] objects = {new Integer(1), new Double(20), dateNow};

        String joinedString = Strings.join(objects);

        assertEquals("1,20.0," + dateNow.toString(), joinedString);
    }

    @Test
    public void shallDetectBlankString() throws Exception {
        assertTrue(isBlank(""));
    }

    @Test
    public void shallDetectNotBlankString() throws Exception {
        assertFalse(isBlank("as"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionIfStringIsBlank() throws Exception {
        requireNotBlank("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionIfStringIsShorterThanLowerBound() throws Exception {
        requireLongerThan("a", 2);
    }

    @Test
    public void shallNotThrowExceptionIfStringIsLongerThanLowerBound() throws Exception {
        requireLongerThan("abc", 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionIfStringIsLongerThanUpperBound() throws Exception {
        requireShorterThan("bca", 2);
    }

    @Test
    public void shallNotThrowExceptionIfStringIsShorterThanUpperBound() throws Exception {
        requireShorterThan("a", 2);
    }
}