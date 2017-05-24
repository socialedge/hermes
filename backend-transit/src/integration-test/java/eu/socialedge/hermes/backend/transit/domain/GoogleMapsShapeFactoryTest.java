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
import org.junit.BeforeClass;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static tec.uom.se.unit.Units.METRE;

public class GoogleMapsShapeFactoryTest {
    private static final double EQUALS_DELTA = 50;

    private static final String API_KEY = "AIzaSyDIc6gwUtax8GCQPsRZ5VAUJWeWMdWQo9w";
    private static final ShapeFactory factory = new GoogleMapsShapeFactory(API_KEY);

    private static final TreeMap<Quantity<Length>, List<Location>> distTraveledLoc = new TreeMap<>();

    @BeforeClass
    public static void setUp() {
        // 5  LOCATIONS
        distTraveledLoc.put(Quantities.getQuantity(BigDecimal.valueOf(1766.0), METRE), new ArrayList<Location>() {{
            add(new Location(48.308245, 11.933289));
            add(new Location(48.307200, 11.927195));
            add(new Location(48.305637, 11.922710));
            add(new Location(48.305066, 11.917459));
            add(new Location(48.304077, 11.912293));
        }});

        // 9  LOCATIONS
        distTraveledLoc.put(Quantities.getQuantity(BigDecimal.valueOf(3157.0), METRE), new ArrayList<Location>() {{
            addAll(distTraveledLoc.get(Quantities.getQuantity(BigDecimal.valueOf(1766.0), METRE)));
            add(new Location(48.300948, 11.908716));
            add(new Location(48.296155, 11.909617));
            add(new Location(48.293985, 11.910999));
            add(new Location(48.294017, 11.912595));
        }});

        // 20 LOCATIONS, 2xLOCATIONS_LIMIT
        distTraveledLoc.put(Quantities.getQuantity(BigDecimal.valueOf(9363.0), METRE), new ArrayList<Location>() {{
            addAll(distTraveledLoc.get(Quantities.getQuantity(BigDecimal.valueOf(3157.0), METRE)));
            add(new Location(48.294362, 11.919408)); //3.7km - LOCATIONS_LIMIT
            add(new Location(48.294928, 11.923893)); //4km
            add(new Location(48.295962, 11.929987)); //4.5km
            add(new Location(48.298174, 11.929995)); //4.8km
            add(new Location(48.296768, 11.936373)); //5.3km
            add(new Location(48.295944, 11.948374)); //7km
            add(new Location(48.294429, 11.956111)); //6.8
            add(new Location(48.293794, 11.962141)); //7.25
            add(new Location(48.291311, 11.967209)); //7.7
            add(new Location(48.291923, 11.976724)); //8.45
            add(new Location(48.290324, 11.989014)); //9.4 - LOCATIONS_LIMIT

        }});

        distTraveledLoc.put(Quantities.getQuantity(BigDecimal.valueOf(10150.0), METRE), new ArrayList<Location>() {{
            addAll(distTraveledLoc.get(Quantities.getQuantity(BigDecimal.valueOf(9363.0), METRE)));
            add(new Location(48.290959, 11.999239));
        }});
    }

    @Test
    public void shouldReturnShapeWithCountOfShapePointsEqualToLocationsCount() {
        val anyLocations = distTraveledLoc.firstEntry().getValue();
        val result = factory.create(anyLocations);

        assertNotNull(result);
        assertEquals(anyLocations.size(), result.getShapePoints().size());
    }

    @Test
    public void shouldReturnShapeWithSameLocationsInSameOrder() {
        val anyLocations = distTraveledLoc.firstEntry().getValue();
        val result = factory.create(anyLocations);

        val resultLocations = result.getShapePoints().stream()
            .map(ShapePoint::getLocation)
            .collect(Collectors.toList());
        assertEquals(anyLocations, resultLocations);
    }

    @Test
    public void shouldReturnShapeWithDistanceUnitsInMeters() {
        val anyLocations = distTraveledLoc.firstEntry().getValue();
        val result = factory.create(anyLocations);

        val allMeters = result.getShapePoints().stream()
            .map(ShapePoint::getDistanceTraveled)
            .map(Quantity::getUnit)
            .allMatch(METRE::equals);
        assertTrue(allMeters);
    }

    @Test
    public void shouldReturnShapeWithCorrectDistances() {
        distTraveledLoc.forEach((distTraveled, locations) -> {
            val calculatedShape = factory.create(locations);
            val calculatedWaypoints = calculatedShape.getShapePoints();
            val calculatedLastWaypoint = calculatedWaypoints.get(calculatedWaypoints.size() - 1);
            val calculatedTotalDistanceTraveled = calculatedLastWaypoint.getDistanceTraveled();

            assertEquals(distTraveled.getValue().doubleValue(),
                        calculatedTotalDistanceTraveled.getValue().doubleValue(),
                        EQUALS_DELTA);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForEmptyLocations() {
        factory.create(Collections.emptyList());
    }

    @Test(expected = ShapeFactoryException.class)
    public void shouldThrowExceptionForIncorrectLocation() {
        factory.create(Arrays.asList(new Location(-33.865143, 151.209900), new Location(-34, 125)));
    }
}
