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
package eu.socialedge.hermes.domain.infrastructure;

import eu.socialedge.hermes.domain.ext.ValueObject;
import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@ValueObject
@Embeddable
public class Waypoint implements Serializable, Comparable<Waypoint> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @Column(name = "position")
    private int position;

    protected Waypoint() {}

    public Waypoint(Station station, int position) {
        if (position <= 0)
            throw new IllegalArgumentException("position arg must be <= 0");

        this.station = Validate.notNull(station);
        this.position = position;
    }

    public static Waypoint of(Station station, int position) {
        return new Waypoint(station, position);
    }

    public Station getStation() {
        return station;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public int compareTo(Waypoint o) {
        return Integer.compare(this.position, o.position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;
        Waypoint waypoint = (Waypoint) o;
        return getPosition() == waypoint.getPosition() &&
                Objects.equals(getStation(), waypoint.getStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStation(), getPosition());
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                ", station=" + station +
                ", position=" + position +
                '}';
    }
}
