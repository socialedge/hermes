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
package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.transit.LineId;
import eu.socialedge.hermes.domain.transit.RouteId;

import java.util.concurrent.ThreadLocalRandom;

class RandomIdGenerator {

    public static AgencyId randomAgencyId() {
        return AgencyId.of("ag" + randomString());
    }

    public static LineId randomLineId() {
        return LineId.of("ln" + randomString());
    }

    public static RouteId randomRouteId() {
        return RouteId.of("rt" + randomString());
    }

    public static ScheduleId randomScheduleId() {
        return ScheduleId.of("sch" + randomString());
    }

    public static StationId randomStationId() {
        return StationId.of("st" + randomString());
    }

    private static String randomString() {
        return String.valueOf(System.nanoTime())
                + String.valueOf(ThreadLocalRandom.current().nextInt(1, 100));
    }
}
