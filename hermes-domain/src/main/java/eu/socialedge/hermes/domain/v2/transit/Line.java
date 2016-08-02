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
package eu.socialedge.hermes.domain.v2.transit;

import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.v2.shared.transport.VehicleType;
import eu.socialedge.hermes.domain.v2.operator.AgencyId;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Line represents a group of {@link Line#tripIds} that are displayed to
 * riders as a single service handled by {@link Line#agencyId}.
 *
 * @see <a href="https://goo.gl/vusuDu">Google Transit APIs
 * > Static Transit > routes.txt File</a>
 */
@AggregateRoot
public class Line {
    private final LineId lineId;

    private AgencyId agencyId;

    private String name;

    private VehicleType vehicleType;

    private Collection<TripId> tripIds;

    public Line(LineId lineId, String name, AgencyId agencyId, VehicleType vehicleType) {
        this.lineId = notNull(lineId);
        this.name = notBlank(name);
        this.agencyId = notNull(agencyId);
        this.vehicleType = notNull(vehicleType);
        this.tripIds = Collections.emptyList();
    }

    public Line(LineId lineId, String name, AgencyId agencyId,
                VehicleType vehicleType, Collection<TripId> tripIds) {
        this.lineId = notNull(lineId);
        this.name = notBlank(name);
        this.agencyId = notNull(agencyId);
        this.vehicleType = notNull(vehicleType);
        this.tripIds = !isNull(tripIds) ? tripIds : Collections.emptyList();
    }

    public LineId lineId() {
        return lineId;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = notBlank(name);
    }

    public VehicleType vehicleType() {
        return vehicleType;
    }

    public void vehicleType(VehicleType vehicleType) {
        this.vehicleType = notNull(vehicleType);
    }

    public Collection<TripId> tripIds() {
        return tripIds;
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
                ", vehicleType=" + vehicleType +
                ", tripIds=" + tripIds +
                '}';
    }
}
