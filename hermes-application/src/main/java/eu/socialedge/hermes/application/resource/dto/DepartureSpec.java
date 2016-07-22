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
import java.time.LocalTime;

public class DepartureSpec {
    @NotNull
    @Valid
    StationSpec station;

    @NotNull
    LocalTime time;

    public StationSpec getStation() {
        return station;
    }

    public void setStation(StationSpec station) {
        this.station = station;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
