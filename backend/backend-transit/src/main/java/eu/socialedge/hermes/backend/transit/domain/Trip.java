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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * A Trip represents a journey taken by a vehicle through Stops. Trips
 * are time-specific — they are defined as a sequence of StopTimes, so
 * a single Trip represents one journey along a transit route.
 */
@Document
@ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Trip  {

    @DBRef
    private @NotNull Route route;

    private @NotNull Integer vehicleId;

    private String headsign;

    private List<Stop> stops;

    public Trip(Route route, Integer vehicleId, String headsign, List<Stop> stops) {
        this.route = notNull(route);
        this.vehicleId = notNull(vehicleId);
        this.headsign = headsign;
        this.stops = new ArrayList<>(notEmpty(stops));
    }

    public Trip(Route route, Integer vehicleId, List<Stop> stops) {
        this(route, vehicleId, null, stops);
    }

    public void setRoute(Route route) {
        this.route = notNull(route);
    }

    public boolean addStop(Stop stop) {
        if (stops.contains(stop))
            return false;

        return stops.add(stop);
    }

    public boolean addStop(Stop stop, int index) {
        if (stops.contains(stop))
            return false;

        stops.add(index, stop);
        return true;
    }

    public void removeStop(Stop stop) {
        stops.remove(stop);
    }

    public List<Stop> getStops() {
        return Collections.unmodifiableList(stops);
    }
}
