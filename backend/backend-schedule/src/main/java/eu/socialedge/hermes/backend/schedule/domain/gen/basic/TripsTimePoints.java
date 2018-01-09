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

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.INBOUND;
import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.OUTBOUND;

class TripsTimePoints {
    private final List<TimePoint> timePoints = new ArrayList<>();
    private final Duration minLayover;
    private final boolean isBidirectional;

    TripsTimePoints(LocalTime startTimeInbound, LocalTime startTimeOutbound, LocalTime endTimeInbound, LocalTime endTimeOutbound, Duration minLayover, Duration headway) {
        this.isBidirectional = true;
        this.minLayover = minLayover;
        timePoints.addAll(generateTimePoints(startTimeInbound, endTimeInbound, INBOUND, headway));
        timePoints.addAll(generateTimePoints(startTimeOutbound, endTimeOutbound, OUTBOUND, headway));
        timePoints.sort(Comparator.comparing(TimePoint::getTime));
    }

    TripsTimePoints(LocalTime startTimeInbound, LocalTime endTimeInbound, Duration minLayover, Duration headway) {
        this.isBidirectional = false;
        this.minLayover = minLayover;
        timePoints.addAll(generateTimePoints(startTimeInbound, endTimeInbound, INBOUND, headway));
        timePoints.sort(Comparator.comparing(TimePoint::getTime));
    }

    Optional<TimePoint> findFirstNotServicedTimePoint() {
        return timePoints.stream()
            .filter(timePoint -> !timePoint.isServiced())
            .findFirst();
    }

    Optional<TimePoint> findNextNotServicedTimePointAfter(LocalTime time, TimePoint timePoint) {
        return timePoints.stream()
            .filter(point -> !point.isServiced())
            .filter(point -> isBidirectional ^ point.getDirection().equals(timePoint.getDirection()))
            .filter(point -> point.getTime().isAfter(time))
            .filter(point -> !Duration.between(time, point.getTime()).minus(minLayover).isNegative())
            .findFirst();
    }

    private static List<TimePoint> generateTimePoints(LocalTime startTime, LocalTime endTime, Direction direction, Duration headway) {
        List<TimePoint> timePoints = new ArrayList<>();
        for (LocalTime nextTimePoint = startTime; nextTimePoint.isBefore(endTime); nextTimePoint = nextTimePoint.plus(headway)) {
            timePoints.add(new TimePoint(direction, nextTimePoint,false));
        }
        return timePoints;
    }

}
