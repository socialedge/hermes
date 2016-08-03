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
package eu.socialedge.hermes.application.v2.resource.spec;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AgencySpecification {
    @NotNull
    @Size(min = 1)
    public String agencyId;

    @NotNull
    @Size(min = 1)
    public String name;

    @NotNull
    @Size(min = 5)
    public String website;

    @NotNull
    @Size(min = 4)
    public String timeZoneOffset;

    @NotNull
    public Float locationLatitude;

    @NotNull
    public Float locationLongitude;

    @Size(min = 2)
    public String phone;

    @Size(min = 3)
    public String email;
}
