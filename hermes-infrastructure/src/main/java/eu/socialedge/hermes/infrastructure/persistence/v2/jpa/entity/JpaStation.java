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

import eu.socialedge.hermes.domain.v2.infrastructure.TransportType;

import java.util.Objects;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "stations")
public class JpaStation {

    @Id
    @Column(name = "station_id")
    private String stationId;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    private JpaLocation location;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "stations_transport_types",
                     joinColumns = @JoinColumn(name = "station_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_types", nullable = false)
    private Set<TransportType> transportTypes;

    public JpaStation() {}

    public String stationId() {
        return stationId;
    }

    public void stationId(String stationId) {
        this.stationId = stationId;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public JpaLocation location() {
        return location;
    }

    public void location(JpaLocation location) {
        this.location = location;
    }

    public Set<TransportType> transportTypes() {
        return transportTypes;
    }

    public void transportTypes(Set<TransportType> transportTypes) {
        this.transportTypes = transportTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaStation)) return false;
        JpaStation that = (JpaStation) o;
        return Objects.equals(stationId, that.stationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationId);
    }
}
