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
package eu.socialedge.hermes.backend.transit.domain;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.BeforeClass;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static tec.uom.se.unit.Units.METRE;

public class GMapsTravelDistanceMeterTest {
    private static final double EQUALS_DELTA = 10;

    private static final String API_KEY = "AIzaSyDIc6gwUtax8GCQPsRZ5VAUJWeWMdWQo9w";
    private static final TravelDistanceMeter distanceMeter = new GMapsTravelDistanceMeter(API_KEY);

    private static final Map<Pair<Location, Location>, Quantity<Length>> distances = new LinkedHashMap<>();

    @BeforeClass
    public static void setUp() {
        distances.put(Pair.of(new Location(48.308245, 11.933289),
                              new Location(48.307200, 11.927195)),
            Quantities.getQuantity(481, METRE));

        distances.put(Pair.of(new Location(48.176246, 11.540462),
                              new Location(48.1663378,11.5379251)),
            Quantities.getQuantity(1125, METRE));

        distances.put(Pair.of(new Location(48.175775, 11.521898),
                              new Location(48.1638352, 11.507056)),
            Quantities.getQuantity(3029, METRE));

        distances.put(Pair.of(new Location(50.9116847, 34.7873203),
                              new Location(50.9139058, 34.7753419)),
            Quantities.getQuantity(875, METRE));

        distances.put(Pair.of(new Location(50.9117361, 34.7769519),
                              new Location(50.9139142, 34.7782609)),
            Quantities.getQuantity(266, METRE));
    }

    @Test
    public void calculateDirectDistanceCorrectly() {
        distances.forEach((origDest, expectedDistance) -> {
            Quantity<Length> actualDistance = distanceMeter.calculate(origDest.getLeft(), origDest.getRight());

            assertEquals(expectedDistance.getValue().doubleValue(), actualDistance.getValue().doubleValue(), EQUALS_DELTA);
        });
    }

    @Test
    public void calculateBulkDistancesCorrectly() {
        val origDestSet = distances.keySet();
        val origins = origDestSet.stream().map(Pair::getLeft).collect(toList());
        val destinations = origDestSet.stream().map(Pair::getRight).collect(toList());

        Map<Pair<Location, Location>, Quantity<Length>> actualDistances = distanceMeter.calculate(origins, destinations);

        assertEquals(distances, actualDistances);
    }
}
