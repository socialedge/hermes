/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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
package eu.socialedge.hermes.backend.schedule.domain.gen;

import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.val;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DefaultStopFactoryTest {

    private StopFactory stopFactory = new DefaultStopFactory();

    @Test
    public void shouldSetArrivalTimeAsProvided() {
        val station = new Station("1", new HashSet<VehicleType>(){{add(VehicleType.BUS);}},
            Location.of(1.1, 1.1), Duration.ofSeconds(10));
        val arrivalTime = LocalTime.now();

        val stop = stopFactory.create(arrivalTime, station);

        assertNotNull(stop);
        assertEquals(arrivalTime, stop.getArrival());
    }

    @Test
    public void shouldUseStationDwellForDwellTime() {
        val station = new Station("1", new HashSet<VehicleType>(){{add(VehicleType.BUS);}},
            Location.of(1.1, 1.1), Duration.ofSeconds(10));

        val stop = stopFactory.create(LocalTime.now(), station);

        assertEquals(station.getDwell(), Duration.between(stop.getArrival(), stop.getDeparture()));
    }

    @Test
    public void shouldSetProvidedStation() {
        val station = new Station("1", new HashSet<VehicleType>(){{add(VehicleType.BUS);}},
            Location.of(1.1, 1.1), Duration.ofSeconds(10));

        val stop = stopFactory.create(LocalTime.now(), station);

        assertEquals(station, stop.getStation());
    }
}
