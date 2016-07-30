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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class JpaStop implements Comparable<JpaStop>, Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private JpaStation station;

    @Column(name = "arrival", nullable = false)
    private LocalTime arrival;

    @Column(name = "departure", nullable = false)
    private LocalTime departure;

    @Column(name = "position", nullable = false)
    private int position;

    public JpaStop() {}

    public JpaStation station() {
        return station;
    }

    public void station(JpaStation station) {
        this.station = station;
    }

    public int position() {
        return position;
    }

    public void position(int position) {
        this.position = position;
    }

    public LocalTime arrival() {
        return arrival;
    }

    public void arrival(LocalTime arrival) {
        this.arrival = arrival;
    }

    public LocalTime departure() {
        return departure;
    }

    public void departure(LocalTime departure) {
        this.departure = departure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaStop)) return false;
        JpaStop that = (JpaStop) o;
        return position == that.position &&
                Objects.equals(station, that.station) &&
                Objects.equals(arrival, that.arrival) &&
                Objects.equals(departure, that.departure);
    }

    @Override
    public int hashCode() {
        return Objects.hash(station, arrival, departure, position);
    }

    @Override
    public String toString() {
        return "JpaStop{" +
                "station=" + station +
                ", arrival=" + arrival +
                ", departure=" + departure +
                ", position=" + position +
                '}';
    }

    @Override
    public int compareTo(JpaStop o) {
        return Integer.compare(this.position(), o.position());
    }
}
