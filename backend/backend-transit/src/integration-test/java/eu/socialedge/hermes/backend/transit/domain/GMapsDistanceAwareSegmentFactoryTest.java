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

import lombok.val;
import org.junit.Test;
import tec.uom.se.quantity.Quantities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static tec.uom.se.unit.Units.METRE;

public class GMapsDistanceAwareSegmentFactoryTest {

    private static final double EQUALS_DELTA = 10;

    private static final String API_KEY = "AIzaSyDIc6gwUtax8GCQPsRZ5VAUJWeWMdWQo9w";
    private static final TravelDistanceMeter distanceMeter = new GMapsTravelDistanceMeter(API_KEY);

    private static final DistanceAwareSegmentFactory segmentFactory = new DistanceAwareSegmentFactory(distanceMeter);

    private static final List<Segment> validSegments = new ArrayList<Segment>() {{
        {
            val begin = mock(Station.class);
            when(begin.getLocation()).thenReturn(Location.of(48.308245, 11.933289));

            val end = mock(Station.class);
            when(end.getLocation()).thenReturn(Location.of(48.307200, 11.927195));

            add(Segment.of(begin, end, Quantities.getQuantity(481, METRE)));
        }

        {
            val begin = mock(Station.class);
            when(begin.getLocation()).thenReturn(Location.of(48.176246, 11.540462));

            val end = mock(Station.class);
            when(end.getLocation()).thenReturn(Location.of(48.1663378,11.5379251));

            add(Segment.of(begin, end, Quantities.getQuantity(1125, METRE)));
        }

        {
            val begin = mock(Station.class);
            when(begin.getLocation()).thenReturn(Location.of(50.9151736,34.7790436));

            val end = mock(Station.class);
            when(end.getLocation()).thenReturn(Location.of(50.9119775,34.7857649));

            val waypoints = new ArrayList<Location>() {{
               add(Location.of(50.9124336,34.7773585));
                add(Location.of(50.9111219,34.7799957));
            }};

            add(Segment.of(begin, end, Quantities.getQuantity(1199, METRE), waypoints));
        }
    }};

    @Test
    public void calculatesCorrectDistances() {
        validSegments.forEach(s -> {
            val expectedDistance = s.getLength();
            val actualDistance = segmentFactory.factory(s.getBegin(), s.getEnd(), s.getWaypoints()).getLength();

            assertEquals(expectedDistance.getValue().doubleValue(), actualDistance.getValue().doubleValue(), EQUALS_DELTA);
        });
    }
}
