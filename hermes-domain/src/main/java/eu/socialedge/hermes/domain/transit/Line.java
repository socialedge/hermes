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
import eu.socialedge.hermes.domain.operator.AgencyId;
import eu.socialedge.hermes.domain.shared.Identifiable;
import eu.socialedge.hermes.domain.transport.VehicleType;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static eu.socialedge.hermes.util.Strings.requireNotBlank;
import static eu.socialedge.hermes.util.Values.requireNotNull;

/**
 * Line represents a group of {@link Route}s that are displayed to
 * riders as a single service handled by
 * {@link eu.socialedge.hermes.domain.operator.Agency}.
 *
 * @see <a href="https://goo.gl/vusuDu">Google Transit APIs
 * > Static Transit > routes.txt File</a>
 */
@AggregateRoot
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id") @ToString
@Entity @Table(name = "lines")
public class Line implements Identifiable<LineId> {

    @EmbeddedId
    private final LineId id;

    @Embedded
    private AgencyId agencyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "line_routes", joinColumns = @JoinColumn(name = "line_id"))
    private final Set<RouteId> routeIds;

    public Line(LineId id, AgencyId agencyId, String name, VehicleType vehicleType) {
        this(id, agencyId, name, vehicleType, null, null);
    }

    public Line(LineId id, AgencyId agencyId, String name,
                VehicleType vehicleType, Set<RouteId> routeIds) {
        this(id, agencyId, name, vehicleType, null, routeIds);
        this.vehicleType = vehicleType;
    }

    public Line(LineId id, AgencyId agencyId, String name, VehicleType vehicleType,
                String description, Set<RouteId> routeIds) {
        this.id = requireNotNull(id);
        this.agencyId = requireNotNull(agencyId);
        this.name = requireNotBlank(name);
        this.vehicleType = requireNotNull(vehicleType);
        this.description = description;
        this.routeIds = requireNotNull(routeIds, new HashSet<>());
    }

    @Override
    public LineId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = requireNotBlank(name);
    }

    public String description() {
        return description;
    }

    public void description(String description) {
        this.description = description;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public void agencyId(AgencyId agencyId) {
        this.agencyId = requireNotNull(agencyId);
    }

    public VehicleType vehicleType() {
        return vehicleType;
    }

    public void vehicleType(VehicleType vehicleType) {
        this.vehicleType = requireNotNull(vehicleType);
    }

    public Set<RouteId> attachedRouteIds() {
        return routeIds;
    }
}
