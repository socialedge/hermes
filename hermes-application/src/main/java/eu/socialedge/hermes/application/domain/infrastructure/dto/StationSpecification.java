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
package eu.socialedge.hermes.application.domain.infrastructure.dto;

import eu.socialedge.hermes.application.domain.geo.dto.LocationSpecification;

import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class StationSpecification {
    @NotNull
    @Size(min = 1)
    public String id;

    @NotNull
    @Size(min = 1)
    public String name;

    @Valid
    public LocationSpecification location = new LocationSpecification();

    @NotNull
    @Size(min = 1)
    public Set<String> vehicleTypes;
}
