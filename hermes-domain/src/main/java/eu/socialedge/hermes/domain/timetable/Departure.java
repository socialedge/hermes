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
import eu.socialedge.hermes.domain.infrastructure.Station;
import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

@ValueObject
@Embeddable
public class Departure implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @Column(name = "time")
    private LocalTime time;

    protected Departure() {}

    public Departure(Station station, LocalTime time) {
        this.station = Validate.notNull(station);
        this.time = Validate.notNull(time);
    }

    public static Departure of(Station station, LocalTime time) {
        return new Departure(station, time);
    }


    public Station getStation() {
        return station;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Departure departure = (Departure) o;
        return Objects.equals(station, departure.station) &&
                Objects.equals(time, departure.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station, time);
    }

    @Override
    public String toString() {
        return "Departure{" +
                "station=" + station +
                ", time=" + time +
                '}';
    }
}
