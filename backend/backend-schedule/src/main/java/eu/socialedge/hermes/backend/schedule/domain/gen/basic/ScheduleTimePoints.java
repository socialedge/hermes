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
package eu.socialedge.hermes.backend.schedule.domain.gen.basic;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
class ScheduleTimePoints {
    private final List<TimePoint> timePoints;
    private final Duration minLayover;

    Optional<TimePoint> findNextNotServicedTimePoint() {
        return timePoints.stream()
            .filter(timePoint -> !timePoint.isServiced())
            .findFirst();
    }

    Optional<TimePoint> findNextNotServicedTimePointAfter(LocalTime time, Direction direction) {
        return timePoints.stream()
            .filter(point -> !point.isServiced())
            .filter(point -> point.getDirection().equals(direction))
            .filter(point -> point.getTime().isAfter(time))
            .filter(point -> isInTimeToTravel(time, point))
            .findFirst();
    }

    private boolean isInTimeToTravel(LocalTime from, TimePoint toPoint) {
        return !Duration.between(from, toPoint.getTime()).minus(minLayover).isNegative();
    }
}
