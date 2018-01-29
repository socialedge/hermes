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

import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.service.Route;

import java.time.LocalTime;

public interface TripFactory {

    Trip create(LocalTime startTime, String headsign, Route route);

    default Trip create(LocalTime startTime, Route route) {
        return create(startTime, null, route);
    }
}
