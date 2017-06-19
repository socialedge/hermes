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

import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalTime;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * A {@code Stop} defines when a vehicle arrives at a {@link Station},
 * how long it stays there, and when it departs.
 *
 * @see <a href="https://goo.gl/uz7Ekj">
 *     Google Static Transit (GTFS) - stop_times.txt File</a>
 */
@Document
@EqualsAndHashCode @ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Stop implements Serializable {

    @Getter
    private final @NotNull LocalTime arrival;

    @Getter
    private final @NotNull LocalTime departure;

    @Getter
    @DBRef
    private final @NotNull Station station;

    public Stop(LocalTime arrival, LocalTime departure, Station station) {
        if (arrival.isAfter(departure))
            throw new IllegalArgumentException("Arrival time cant be after departure time");

        this.arrival = arrival;
        this.departure = departure;
        this.station = notNull(station);
    }

    public static Stop of(LocalTime arrival, LocalTime departure, Station station) {
        return new Stop(arrival, departure, station);
    }

    public Stop(LocalTime departure, Station station) {
        this(departure, departure, station);
    }

    public static Stop of(LocalTime departure, Station station) {
        return new Stop(departure, station);
    }
}
