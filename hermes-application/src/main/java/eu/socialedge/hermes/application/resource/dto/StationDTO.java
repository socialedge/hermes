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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class StationDTO {
    @NotNull
    @Size(min = 1)
    private String codeId;

    @NotNull
    @Size(min = 1)
    private String name;

    @NotNull
    private TransportType transportType;

    @NotNull
    private PositionDTO position;

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
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

    public PositionDTO getPositionDTO() {
        return position;
    }

    public void setPositionDTO(PositionDTO position) {
        this.position = position;
    }
}
