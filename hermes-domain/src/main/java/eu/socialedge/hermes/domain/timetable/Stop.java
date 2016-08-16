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
package eu.socialedge.hermes.domain.timetable;

import eu.socialedge.hermes.domain.ext.ValueObject;
import eu.socialedge.hermes.domain.infrastructure.StationId;

import java.io.Serializable;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static eu.socialedge.hermes.util.Values.requireNotNull;

/**
 * Describes a stop on the {@link Trip} and defines when a
 * vehicle arrives at a location, how long it stays there,
 * and when it departs.
 */
@ValueObject
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode @ToString
@Embeddable
public class Stop implements Serializable {

    @Column(name = "station_id", nullable = false)
    private final StationId stationId;

    @Column(name = "arrival", nullable = false)
    private final LocalTime arrival;

    @Column(name = "departure", nullable = false)
    private final LocalTime departure;

    public Stop(StationId stationId, LocalTime arrival, LocalTime departure) {
        this.arrival = requireNotNull(arrival);
        this.departure = requireNotNull(departure);
        this.stationId = requireNotNull(stationId);
    }

    public static Stop of(StationId stationId, LocalTime arrival, LocalTime departure) {
        return new Stop(stationId, arrival, departure);
    }

    public StationId stationId() {
        return stationId;
    }

    public LocalTime arrival() {
        return arrival;
    }

    public LocalTime departure() {
        return departure;
    }
}
