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

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static eu.socialedge.hermes.domain.shared.util.Objects.reqNotNull;
import static eu.socialedge.hermes.domain.shared.util.Strings.reqNotBlank;
import static java.util.Objects.isNull;

/**
 * Line represents a group of {@link Route}s that are displayed to
 * riders as a single service handled by
 * {@link eu.socialedge.hermes.domain.operator.Agency}.
 *
 * @see <a href="https://goo.gl/vusuDu">Google Transit APIs
 * > Static Transit > routes.txt File</a>
 */
@AggregateRoot
public class Line implements Identifiable<LineId> {
    private final LineId lineId;

    private AgencyId agencyId;

    private String name;

    private VehicleType vehicleType;

    private Collection<RouteId> routeIds;

    public Line(LineId lineId, String name, AgencyId agencyId, VehicleType vehicleType) {
        this.lineId = reqNotNull(lineId);
        this.name = reqNotBlank(name);
        this.agencyId = reqNotNull(agencyId);
        this.vehicleType = reqNotNull(vehicleType);
        this.routeIds = Collections.emptyList();
    }

    public Line(LineId lineId, String name, AgencyId agencyId,
                VehicleType vehicleType, Collection<RouteId> routeIds) {
        this.lineId = reqNotNull(lineId);
        this.name = reqNotBlank(name);
        this.agencyId = reqNotNull(agencyId);
        this.vehicleType = reqNotNull(vehicleType);
        this.routeIds = !isNull(routeIds) ? routeIds : Collections.emptyList();
    }

    @Override
    public LineId id() {
        return lineId;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = reqNotBlank(name);
    }

    public VehicleType vehicleType() {
        return vehicleType;
    }

    public void vehicleType(VehicleType vehicleType) {
        this.vehicleType = reqNotNull(vehicleType);
    }

    public Collection<RouteId> routeIds() {
        return routeIds;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public void agencyId(AgencyId agencyId) {
        this.agencyId = reqNotNull(agencyId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(lineId, line.lineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineId);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + lineId +
                ", agencyId=" + agencyId +
                ", vehicleType=" + vehicleType +
                ", routeIds=" + routeIds +
                '}';
    }
}
