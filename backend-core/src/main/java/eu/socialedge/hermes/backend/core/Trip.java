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
package eu.socialedge.hermes.backend.core;

import eu.socialedge.hermes.backend.core.ext.Identifiable;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * A Trip represents a journey taken by a vehicle through Stops. Trips
 * are time-specific — they are defined as a sequence of StopTimes, so
 * a single Trip represents one journey along a transit line or route.
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

    @Getter @Setter
    @Column(name = "headsign")
    private String headsign;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "trip_stop_times", joinColumns = @JoinColumn(name = "trip_id"))
	private Set<StopTime> stopTimes;

    public Trip(Direction direction, String headsign, Set<StopTime> stopTimes) {
        this.direction = notNull(direction);
        this.headsign = headsign;
        this.stopTimes = new HashSet<>(notEmpty(stopTimes));
    }

    public Trip(Direction direction, Set<StopTime> stopTimes) {
        this(direction, null, stopTimes);
    }

    public void direction(Direction direction) {
        this.direction = notNull(direction);
    }

    public void addStopTime(StopTime stopTime) {
        stopTimes.add(stopTime);
    }

    public void removeStopTime(StopTime stopTime) {
        stopTimes.remove(stopTime);
    }

    public Set<StopTime> stopTimes() {
        return Collections.unmodifiableSet(stopTimes);
    }
}
