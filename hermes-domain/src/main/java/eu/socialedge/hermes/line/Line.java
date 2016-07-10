/**
 * Hermes - a Public Transport Management System
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
package eu.socialedge.hermes.line;

import eu.socialedge.hermes.route.Route;
import eu.socialedge.hermes.TransportType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "lines")
public class Line implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private int lineId;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private Operator operator;

    @NotNull
    @Size(min = 2)
    @Column(name = "name")
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TransportType transportType;

    @OneToMany(mappedBy = "route")
    private Set<Route> routes;

    Line() {}

    public Line(String name, TransportType transportType) {
        this.name = name;
        this.transportType = transportType;
    }

    public Line(Operator operator, String name, TransportType transportType) {
        this(name, transportType);
        this.operator = operator;
    }

    public int getLineId() {
        return lineId;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public void addRoute(Route route) {
        this.routes.add(route);
    }

    public void removeRoute(Route route) {
        this.routes.remove(route);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Line)) return false;
        Line line = (Line) o;
        return Objects.equals(getName(), line.getName()) &&
                getTransportType() == line.getTransportType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getTransportType());
    }

    @Override
    public String toString() {
        return "Line{" +
                "lineId=" + lineId +
                ", operator=" + operator +
                ", routes=" + routes +
                ", name='" + name + '\'' +
                ", transportType=" + transportType +
                '}';
    }
}
