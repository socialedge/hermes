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

import eu.socialedge.hermes.domain.infrastructure.TransportType;

import java.util.Collection;

public class LineDetailedDTO {
    private String codeId;
    private TransportType transportType;
    private OperatorBriefDTO operatorBrief;
    private Collection<RouteBriefDTO> routesBrief;

    public LineDetailedDTO() {
    }

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public void setTransportType(TransportType transportType) {
        this.transportType = transportType;
    }

    public OperatorBriefDTO getOperatorBrief() {
        return operatorBrief;
    }

    public void setOperatorBrief(OperatorBriefDTO operatorBrief) {
        this.operatorBrief = operatorBrief;
    }

    public Collection<RouteBriefDTO> getRoutesBrief() {
        return routesBrief;
    }

    public void setRoutesBrief(Collection<RouteBriefDTO> routesBrief) {
        this.routesBrief = routesBrief;
    }
}
