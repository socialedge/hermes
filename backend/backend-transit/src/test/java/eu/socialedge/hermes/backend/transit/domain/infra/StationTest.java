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

package eu.socialedge.hermes.backend.transit.domain.infra;

import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StationTest {

    private static final ArrayList<List<Dwell>> overlappingDwells = new ArrayList<List<Dwell>>() {{
        add(new ArrayList<Dwell>() {{
            add(Dwell.regular(LocalTime.MIN, LocalTime.NOON, Duration.ofMinutes(1)));
            add(Dwell.regular(LocalTime.NOON.plusHours(1), LocalTime.NOON.plusHours(2), Duration.ofMinutes(1)));
            add(Dwell.regular(LocalTime.NOON.plusHours(2), LocalTime.NOON.plusHours(4), Duration.ofMinutes(1))); // ov2
            add(Dwell.regular(LocalTime.NOON.plusHours(3), LocalTime.NOON.plusHours(5), Duration.ofMinutes(1))); // ov 1
        }});
        add(new ArrayList<Dwell>() {{
            add(Dwell.regular(LocalTime.NOON.plusHours(3), LocalTime.NOON.plusHours(5), Duration.ofMinutes(1))); // ov 1
            add(Dwell.regular(LocalTime.MIN, LocalTime.NOON, Duration.ofMinutes(1)));
            add(Dwell.regular(LocalTime.NOON.plusHours(1), LocalTime.NOON.plusHours(2), Duration.ofMinutes(1)));
            add(Dwell.regular(LocalTime.NOON.plusHours(2), LocalTime.NOON.plusHours(4), Duration.ofMinutes(1))); // ov 2
        }});
        add(new ArrayList<Dwell>() {{
            add(Dwell.regular(LocalTime.MIN, LocalTime.NOON, Duration.ofMinutes(1)));
            add(Dwell.regular(LocalTime.NOON.plusHours(1), LocalTime.NOON.plusHours(2), Duration.ofMinutes(1)));
            add(Dwell.regular(LocalTime.NOON.plusHours(3), LocalTime.NOON.plusHours(5), Duration.ofMinutes(1))); // ov 1
            add(Dwell.regular(LocalTime.NOON.plusHours(2), LocalTime.NOON.plusHours(4), Duration.ofMinutes(1))); // ov 2
        }});
    }};

    @Test
    public void ensureNoOverlappingDwells() {
        overlappingDwells.forEach(dwells -> {
            assertThatThrownBy(() -> {
                new Station("n", singleton(VehicleType.BUS), Location.of(20, 20), dwells);
            }).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("overlap");
        });
    }
}
