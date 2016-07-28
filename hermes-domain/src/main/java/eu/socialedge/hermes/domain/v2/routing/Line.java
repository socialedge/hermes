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
package eu.socialedge.hermes.domain.v2.routing;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.v2.infrastructure.TransportType;
import eu.socialedge.hermes.domain.v2.operator.AgencyId;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Line represents a group of {@link Line#routeIds} that are displayed to
 * riders as a single service handled by {@link Line#agencyId}.
 */
@AggregateRoot
public class Line {
    private final LineId lineId;

    private AgencyId agencyId;

    private TransportType transportType;

    private Collection<RouteId> routeIds;

    public Line(LineId lineId, AgencyId agencyId, TransportType transportType) {
        this.lineId = notNull(lineId);
        this.agencyId = notNull(agencyId);
        this.transportType = notNull(transportType);
        this.routeIds = Collections.emptyList();
    }

    public Line(String lineId, AgencyId agencyId, TransportType transportType) {
        this(LineId.of(lineId), agencyId, transportType);
    }

    public Line(LineId lineId, AgencyId agencyId, TransportType transportType,
                Collection<RouteId> routeIds) {
        this.lineId = notNull(lineId);
        this.agencyId = notNull(agencyId);
        this.transportType = notNull(transportType);
        this.routeIds = !isNull(routeIds) ? routeIds : Collections.emptyList();
    }

    public Line(String lineId, AgencyId agencyId, TransportType transportType,
                Collection<RouteId> routeIds) {
        this(LineId.of(lineId), agencyId, transportType, routeIds);
    }

    public LineId lineId() {
        return lineId;
    }

    public TransportType transportType() {
        return transportType;
    }

    public Collection<RouteId> routeIds() {
        return routeIds;
    }

    public AgencyId agencyId() {
        return agencyId;
    }

    public void agencyId(AgencyId agencyId) {
        this.agencyId = notNull(agencyId);
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
                "lineId=" + lineId +
                ", agencyId=" + agencyId +
                ", transportType=" + transportType +
                ", routeIds=" + routeIds +
                '}';
    }
}
