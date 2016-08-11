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
package eu.socialedge.hermes.domain.transit;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.infrastructure.StationId;
import eu.socialedge.hermes.domain.shared.Identifiable;
import eu.socialedge.hermes.domain.transport.VehicleType;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static eu.socialedge.hermes.util.Values.requireNotNull;

@AggregateRoot
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id") @ToString
@Entity @Table(name = "routes")
public class Route implements Identifiable<RouteId> {

    @EmbeddedId
    private final RouteId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "route_stations", joinColumns = @JoinColumn(name = "route_id"))
    @OrderColumn(name = "station_order", nullable = false)
    private final List<StationId> stationIds;

    public Route(RouteId id, VehicleType vehicleType) {
        this(id, vehicleType, null);
    }

    public Route(RouteId id, VehicleType vehicleType, List<StationId> stationIds) {
        this.id = requireNotNull(id);
        this.vehicleType = requireNotNull(vehicleType);
        this.stationIds = requireNotNull(stationIds, new LinkedList<>());
    }

    @Override
    public RouteId id() {
        return id;
    }

    public VehicleType vehicleType() {
        return vehicleType;
    }

    public void vehicleType(VehicleType vehicleType) {
        this.vehicleType = requireNotNull(vehicleType);
    }

    public List<StationId> stationIds() {
        return stationIds;
    }
}
