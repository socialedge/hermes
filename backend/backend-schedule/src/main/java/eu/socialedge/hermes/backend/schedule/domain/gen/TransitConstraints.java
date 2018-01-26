/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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
package eu.socialedge.hermes.backend.schedule.domain.gen;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalTime;

import static org.apache.commons.lang3.Validate.notNull;

@Getter
public class TransitConstraints {
    private LocalTime startTimeInbound;
    private LocalTime endTimeInbound;
    private LocalTime startTimeOutbound;
    private LocalTime endTimeOutbound;
    private Duration headway;
    private Duration minLayover;

    public TransitConstraints(LocalTime startTimeInbound, LocalTime endTimeInbound, Duration headway, Duration minLayover) {
        this.startTimeInbound = notNull(startTimeInbound);
        this.endTimeInbound = notNull(endTimeInbound);
        this.headway = notNull(headway);
        this.minLayover = notNull(minLayover);
    }

    public TransitConstraints(LocalTime startTimeInbound, LocalTime endTimeInbound, LocalTime startTimeOutbound, LocalTime endTimeOutbound, Duration headway, Duration minLayover) {
        this(startTimeInbound, endTimeInbound, headway, minLayover);
        this.startTimeOutbound = notNull(startTimeOutbound);
        this.endTimeOutbound = notNull(endTimeOutbound);
    }

    public boolean isBidirectional() {
        return startTimeOutbound != null && endTimeOutbound != null;
    }
}
