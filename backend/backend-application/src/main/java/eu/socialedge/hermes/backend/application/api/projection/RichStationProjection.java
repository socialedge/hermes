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
package eu.socialedge.hermes.backend.application.api.projection;

import eu.socialedge.hermes.backend.transit.domain.VehicleType;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;

import java.time.Duration;
import java.util.Set;

public interface RichStationProjection {

    String getName();

    String getDescription();

    Set<VehicleType> getVehicleTypes();

    Location getLocation();

    Duration getDwell();

}
