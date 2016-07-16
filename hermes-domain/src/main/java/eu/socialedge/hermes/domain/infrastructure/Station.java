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

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import org.apache.commons.lang3.Validate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@AggregateRoot
@Table(name = "stations")
public class Station implements Serializable {
    @Id
    @Column(name = "station_id")
    private String stationCodeId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransportType transportType;

    @Embedded
    private Position position;

    protected Station() {}

    public Station(String stationCodeId, String name, TransportType transportType) {
        this.stationCodeId = Validate.notBlank(stationCodeId);
        this.name = Validate.notBlank(name);
        this.transportType = Validate.notNull(transportType);
    }

    public String getStationCodeId() {
        return stationCodeId;
    }

    public String getName() {
        return name;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station station = (Station) o;
        return Objects.equals(getStationCodeId(), station.getStationCodeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStationCodeId());
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationCodeId='" + stationCodeId + '\'' +
                ", name='" + name + '\'' +
                ", transportType=" + transportType +
                ", position=" + position +
                '}';
    }
}
