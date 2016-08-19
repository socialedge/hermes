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

import eu.socialedge.hermes.domain.infrastructure.dto.StationSpecification;
import eu.socialedge.hermes.domain.infrastructure.dto.StationSpecificationMapper;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.transport.VehicleType;
import org.junit.Test;

import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class StationSpecificationMapperTest {

    private StationSpecificationMapper stationDataMapper = new StationSpecificationMapper();

    @Test
    public void testToData() {
        Station station = new Station(StationId.of("stationId"), "station", new Location(10, 10), new HashSet<VehicleType>() {{
            add(VehicleType.BUS);
            add(VehicleType.LOCAL_BUS);
            add(VehicleType.HIGH_SPEED_RAIL);
            add(VehicleType.SLEEPER_RAIL);
        }});

        StationSpecification spec = stationDataMapper.toDto(station);

        assertEquals(station.id().toString(), spec.id);
        assertEquals(station.name(), spec.name);
        assertEquals(station.location().latitude(), spec.location.latitude, 0.0);
        assertEquals(station.location().longitude(), spec.location.longitude, 0.0);
        assertEquals(station.vehicleTypes(), spec.vehicleTypes.stream().map(VehicleType::valueOf)
                .collect(Collectors.toSet()));
    }

    @Test
    public void testFromData() {
        StationSpecification spec = new StationSpecification();
        spec.id = "stationId";
        spec.name = "station";
        spec.location.latitude = 10f;
        spec.location.longitude = 10f;
        spec.vehicleTypes = new HashSet<String>() {{
            add("BUS");
            add("LOCAL_BUS");
            add("HIGH_SPEED_RAIL");
            add("SLEEPER_RAIL");
        }};

        Station station = stationDataMapper.fromDto(spec);


        assertEquals(spec.id, station.id().toString());
        assertEquals(spec.name, station.name());
        assertEquals(spec.location.latitude, station.location().latitude(), 0.0);
        assertEquals(spec.location.longitude, station.location().longitude(), 0.0);
        assertEquals(spec.vehicleTypes.stream().map(VehicleType::valueOf)
                .collect(Collectors.toSet()), station.vehicleTypes());
    }
}
