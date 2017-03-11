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

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalTime;

import static org.apache.commons.lang3.Validate.notNull;

@Getter
@Embeddable
@EqualsAndHashCode @ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class StopTime implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "arrival", nullable = false)
    private final LocalTime arrival;

    @Column(name = "departure", nullable = false)
    private final LocalTime departure;

    @ManyToOne
    @JoinColumn(name = "stop_id")
    private final Stop stop;

    public StopTime(LocalTime arrival, LocalTime departure, Stop stop) {
        if (arrival.isAfter(departure))
            throw new IllegalArgumentException("Arrival time cant be after departure time");

        this.arrival = arrival;
        this.departure = departure;
        this.stop = notNull(stop);
    }

    public StopTime(LocalTime departure, Stop stop) {
        this(departure, departure, stop);
    }
}
