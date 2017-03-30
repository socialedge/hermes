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

import eu.socialedge.hermes.backend.transit.domain.ext.Identifiable;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
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
@ToString
@Accessors(fluent = true)
@Entity @Access(AccessType.FIELD)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Trip extends Identifiable<Long> {

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private @NotNull Direction direction;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private @NotNull Route route;

    @Getter @Setter
    @Column(name = "vehicleId")
    private @NotNull Integer vehicleId;

    @Getter @Setter
    @Column(name = "headsign")
    private String headsign;

    @ElementCollection
    @OrderColumn(name = "stop_sequence", nullable = false)
    @CollectionTable(name = "trip_stop_times", joinColumns = @JoinColumn(name = "trip_id"))
	private List<Stop> stops;

    public Trip(Direction direction, Route route, Integer vehicleId, String headsign, List<Stop> stops) {
        this.direction = notNull(direction);
        this.route = notNull(route);
        this.vehicleId = notNull(vehicleId);
        this.headsign = headsign;
        this.stops = new ArrayList<>(notEmpty(stops));
    }

    public Trip(Direction direction, Route route, Integer vehicleId, List<Stop> stops) {
        this(direction, route, vehicleId, null, stops);
    }

    public void direction(Direction direction) {
        this.direction = notNull(direction);
    }

    public void route(Route route) {
        this.route = notNull(route);
    }

    public boolean addStopTime(Stop stop) {
        if (stops.contains(stop))
            return false;

        return stops.add(stop);
    }

    public boolean addStopTime(Stop stop, int index) {
        if (stops.contains(stop))
            return false;

        stops.add(index, stop);
        return true;
    }

    public void removeStopTime(Stop stop) {
        stops.remove(stop);
    }

    public List<Stop> stopTimes() {
        return Collections.unmodifiableList(stops);
    }
}
