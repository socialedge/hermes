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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@AggregateRoot
@Table(name = "lines")
public class Line implements Serializable {
    @Id
    @Column(name = "line_code")
    private String lineCodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private Operator operator;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransportType transportType;

    @OneToMany
    @JoinColumn(name = "line_code")
    private Set<Route> routes = new HashSet<>();

    protected Line() {}

    public Line(String lineCodeId, TransportType transportType) {
        this.lineCodeId = Validate.notBlank(lineCodeId);
        this.transportType = Validate.notNull(transportType);
    }

    public Line(String lineCodeId, TransportType transportType, Set<Route> routes) {
        this(lineCodeId, transportType);
        this.routes = Validate.notEmpty(routes);
    }

    public String getLineCodeId() {
        return lineCodeId;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public boolean addRoute(Route route) {
        return this.routes.add(route);
    }

    public boolean removeRoute(Route route) {
        return this.routes.remove(route);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(getLineCodeId(), line.getLineCodeId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLineCodeId());
    }

    @Override
    public String toString() {
        return "Line{" +
                "lineCodeId='" + lineCodeId + '\'' +
                ", operator=" + operator +
                ", transportType=" + transportType +
                ", routes=" + routes +
                '}';
    }
}
