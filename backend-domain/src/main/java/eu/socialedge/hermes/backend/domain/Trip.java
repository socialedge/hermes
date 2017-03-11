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
package eu.socialedge.hermes.backend.domain;

import eu.socialedge.hermes.backend.domain.ext.Identifiable;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * A Trip represents a journey taken by a vehicle through Stops. Trips
 * are time-specific — they are defined as a sequence of StopTimes, so
 * a single Trip represents one journey along a transit line or route.
 */
@ToString
@Entity @Access(AccessType.FIELD)
@Getter @Setter @Accessors(fluent = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Trip extends Identifiable<Long> {

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    private @NotNull Direction direction;

    @Column(name = "headsign")
    private String headsign;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "trip_stop_times", joinColumns = @JoinColumn(name = "trip_id"))
	private List<StopTime> stopTimes;

    public Trip(Direction direction, String headsign, List<StopTime> stopTimes) {
        this.direction = notNull(direction);
        this.headsign = headsign;
        this.stopTimes = new ArrayList<>(stopTimes);
    }

    public Trip(Direction direction, List<StopTime> stopTimes) {
        this(direction, null, stopTimes);
    }

    public void direction(Direction direction) {
        this.direction = notNull(direction);
    }

    public List<StopTime> stopTimes() {
        return Collections.unmodifiableList(stopTimes);
    }

    public void addStopTime(StopTime stopTime) {
        stopTimes.add(stopTime);
    }

    public void removeStopTime(StopTime stopTime) {
        stopTimes.remove(stopTime);
    }
}
