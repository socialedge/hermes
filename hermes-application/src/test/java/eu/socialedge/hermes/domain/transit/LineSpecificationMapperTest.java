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
package eu.socialedge.hermes.domain.transit;

import eu.socialedge.hermes.domain.transit.dto.LineSpecification;
import eu.socialedge.hermes.domain.transit.dto.LineSpecificationMapper;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.transport.VehicleType;
import org.junit.Test;

import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class LineSpecificationMapperTest {

    private LineSpecificationMapper lineDataMapper = new LineSpecificationMapper();

    @Test
    public void testToData() {
        Line line = new Line(LineId.of("lineId"), AgencyId.of("agencyId"), "line", VehicleType.BUS, new HashSet<RouteId>() {{
            add(RouteId.of("route1"));
            add(RouteId.of("route2"));
            add(RouteId.of("route3"));
        }});

        LineSpecification spec = lineDataMapper.toDto(line);

        assertEquals(line.id().toString(), spec.id);
        assertEquals(line.agencyId().toString(), spec.agencyId);
        assertEquals(line.name(), spec.name);
        assertEquals(line.vehicleType().name(), spec.vehicleType);
        assertEquals(line.attachedRouteIds(), spec.routeIds.stream().map(RouteId::of).collect(Collectors.toSet()));
    }

    @Test
    public void testFromData() {
        LineSpecification spec = new LineSpecification();
        spec.id = "lineId";
        spec.agencyId = "agencyId";
        spec.name = "name";
        spec.vehicleType = VehicleType.BUS.name();
        spec.routeIds = new HashSet<String>() {{
            add("route1");
            add("route2");
            add("route3");
        }};

        Line line = lineDataMapper.fromDto(spec);

        assertEquals(spec.id, line.id().toString());
        assertEquals(spec.agencyId, line.agencyId().toString());
        assertEquals(spec.name, line.name());
        assertEquals(spec.vehicleType, line.vehicleType().name());
        assertEquals(spec.routeIds.stream().map(RouteId::of).collect(Collectors.toSet()), line.attachedRouteIds());
    }
}
