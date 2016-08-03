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

import eu.socialedge.hermes.domain.v2.transport.VehicleType;

import java.util.Collection;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "lines")
public class JpaLine {

    @Id
    @Column(name = "line_id")
    private String lineId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id")
    private JpaAgency agency;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type", nullable = false)
    private VehicleType vehicleType;

    @OneToMany
    @JoinColumn(name = "route_id", referencedColumnName = "line_id")
    private Collection<JpaTrip> trips;

    public JpaLine() {}

    public String lineId() {
        return lineId;
    }

    public void lineId(String lineId) {
        this.lineId = lineId;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public JpaAgency agency() {
        return agency;
    }

    public void agency(JpaAgency agency) {
        this.agency = agency;
    }

    public VehicleType vehicleType() {
        return vehicleType;
    }

    public void vehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Collection<JpaTrip> trips() {
        return trips;
    }

    public void trips(Collection<JpaTrip> trips) {
        this.trips = trips;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaLine)) return false;
        JpaLine jpaLine = (JpaLine) o;
        return Objects.equals(lineId, jpaLine.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId);
    }
}
