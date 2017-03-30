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
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalTime;

import static org.apache.commons.lang3.Validate.notNull;

@Embeddable
@Accessors(fluent = true)
@EqualsAndHashCode @ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Stop implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Column(name = "arrival", nullable = false)
    private final LocalTime arrival;

    @Getter
    @Column(name = "departure", nullable = false)
    private final LocalTime departure;

    @Getter
    @ManyToOne
    @JoinColumn(name = "stop_id")
    private final Station station;

    public Stop(LocalTime arrival, LocalTime departure, Station station) {
        if (arrival.isAfter(departure))
            throw new IllegalArgumentException("Arrival time cant be after departure time");

        this.arrival = arrival;
        this.departure = departure;
        this.station = notNull(station);
    }

    public Stop(LocalTime departure, Station station) {
        this(departure, departure, station);
    }
}
