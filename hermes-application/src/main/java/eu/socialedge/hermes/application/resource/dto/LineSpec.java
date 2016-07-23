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
package eu.socialedge.hermes.application.resource.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;

public class LineSpec {
    @NotNull
    @Size(min = 1)
    String codeId;

    @NotNull
    @Size(min = 1)
    String transportType;

    @Valid
    Collection<RouteSpec> routes = new HashSet<>();

    @Valid
    OperatorSpec operator;

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public Collection<RouteSpec> getRoutes() {
        return routes;
    }

    public void setRoutes(Collection<RouteSpec> routes) {
        this.routes = routes;
    }

    public OperatorSpec getOperator() {
        return operator;
    }

    public void setOperator(OperatorSpec operator) {
        this.operator = operator;
    }
}
