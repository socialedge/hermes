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
package eu.socialedge.hermes.application.domain.infrastructure;

import eu.socialedge.hermes.application.domain.infrastructure.dto.StationSpecification;
import eu.socialedge.hermes.application.domain.infrastructure.dto.StationSpecificationMapper;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationId;
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

        StationSpecification data = stationDataMapper.toDto(station);

        assertEquals(station.id().toString(), data.id);
        assertEquals(station.name(), data.name);
        assertEquals(station.location().latitude(), data.location.latitude, 0.0);
        assertEquals(station.location().longitude(), data.location.longitude, 0.0);
        assertEquals(station.vehicleTypes(), data.vehicleTypes.stream().map(VehicleType::valueOf)
                .collect(Collectors.toSet()));
    }

    @Test
    public void testFromData() {
        StationSpecification data = new StationSpecification();
        data.id = "stationId";
        data.name = "station";
        data.location.latitude = 10f;
        data.location.longitude = 10f;
        data.vehicleTypes = new HashSet<String>() {{
            add("BUS");
            add("LOCAL_BUS");
            add("HIGH_SPEED_RAIL");
            add("SLEEPER_RAIL");
        }};

        Station station = stationDataMapper.fromDto(data);


        assertEquals(data.id, station.id().toString());
        assertEquals(data.name, station.name());
        assertEquals(data.location.latitude, station.location().latitude(), 0.0);
        assertEquals(data.location.longitude, station.location().longitude(), 0.0);
        assertEquals(data.vehicleTypes.stream().map(VehicleType::valueOf)
                .collect(Collectors.toSet()), station.vehicleTypes());
    }
}
