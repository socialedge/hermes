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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static eu.socialedge.hermes.domain.shared.util.Collections.isEmpty;
import static eu.socialedge.hermes.domain.shared.util.Collections.isNotEmpty;
import static eu.socialedge.hermes.domain.shared.util.Collections.requireNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectionsTest {

    @Test
    public void shallDetectEmptyCollectionAsEmptyOne() {
        assertTrue(isEmpty(new ArrayList<>()));
    }

    @Test
    public void shallNotDetectEmptyCollectionAsNotEmptyOne() {
        assertFalse(isNotEmpty(new ArrayList<>()));
    }

    @Test
    public void shallDetectNullCollectionAsEmptyOne() {
        assertTrue(isEmpty(null));
    }

    @Test
    public void shallNotDetectNullCollectionAsNotEmptyOne() {
        assertFalse(isNotEmpty(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shallThrowExceptionForEmptyCollectionIfNotEmptyOneWasRequired() {
        requireNotEmpty(new ArrayList<>());
    }

    @Test
    public void shallReturnTheSameCollectionWasRequiredFor() {
        Collection<String> collection = Arrays.asList("asda");

        assertEquals(collection, requireNotEmpty(collection));
    }
}