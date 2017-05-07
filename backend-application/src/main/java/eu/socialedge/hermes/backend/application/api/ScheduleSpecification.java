/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2017 SocialEdge
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
package eu.socialedge.hermes.backend.application.api;

import eu.socialedge.hermes.backend.schedule.domain.Availability;
import lombok.Getter;
import lombok.experimental.Accessors;

import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;

@Getter @Accessors(fluent = true)
public class ScheduleSpecification {

    @NotNull(message = "Line url must not be null")
    private URL line;

    @NotNull(message = "Description must not be null")
    @Size(min = 1, message = "Description must not be empty")
    private String description;

    @NotNull(message = "Availability must not be null")
    private Availability availability;

    @NotNull(message = "Start time inbound must not be null")
    private LocalTime startTimeInbound;

    @NotNull(message = "End time inbound must not be null")
    private LocalTime endTimeInbound;

    @NotNull(message = "Start time outbound must not be null")
    private LocalTime startTimeOutbound;

    @NotNull(message = "End time outbound must not be null")
    private LocalTime endTimeOutbound;

    @NotNull(message = "Headway must not be null")
    private Duration headway;

    @NotNull(message = "Dwell time must not be null")
    private Duration dwellTime;

    @NotNull(message = "Average speed must not be null")
    private Quantity<Speed> averageSpeed;

    @NotNull(message = "Minimal layover must not be null")
    private Duration minLayover;
}
