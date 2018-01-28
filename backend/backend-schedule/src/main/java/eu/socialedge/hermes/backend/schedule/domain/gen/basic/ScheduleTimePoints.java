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

import eu.socialedge.hermes.backend.schedule.domain.gen.TransitConstraints;
import lombok.val;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.INBOUND;
import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.OUTBOUND;

/**
 * This class represents 'skeleton' for the schedule.
 * Provided with {@link TransitConstraints}, it builds up the list of time points
 * within time boundaries.
 * Each time point is a point of time when vehicle starts next trip through the route
 * Complete schedule is a collection of trips that covers all time points
 */
class ScheduleTimePoints {
    private final List<TimePoint> timePoints = new ArrayList<>();
    private final boolean isBidirectional;
    private final Duration minLayover;
    private final Duration headway;

    ScheduleTimePoints(TransitConstraints transitConstraints) {
        minLayover = transitConstraints.getMinLayover();
        headway = transitConstraints.getHeadway();
        isBidirectional = transitConstraints.isBidirectional();
        timePoints.addAll(generateTimePoints(transitConstraints.getStartTimeInbound(), transitConstraints.getEndTimeInbound(), INBOUND));
        if (isBidirectional) {
            timePoints.addAll(generateTimePoints(transitConstraints.getStartTimeOutbound(), transitConstraints.getEndTimeOutbound(), OUTBOUND));
        }
        timePoints.sort(Comparator.comparing(TimePoint::getTime));
    }

    /**
     * Finds earliest time point (based on time) that is not yet serviced
     *
     * @return next time point
     */
    Optional<TimePoint> findFirstNotServicedTimePoint() {
        return timePoints.stream()
            .filter(timePoint -> !timePoint.isServiced())
            .findFirst();
    }

    /**
     * Finds next time point to move to from provided timePoint, taking into account arrival time
     * If schedule is bi-directional, then opposite direction is chosen for time point
     * Min layover is also considered to make sure it's value is regarded
     *
     * @param time the arrival time from the trip from previous time point
     * @param timePoint previous serviced time point
     * @return next available for trip time point
     */
    Optional<TimePoint> findNextNotServicedTimePointAfter(LocalTime time, TimePoint timePoint) {
        return timePoints.stream()
            .filter(point -> !point.isServiced())
            .filter(point -> isBidirectional ^ point.getDirection().equals(timePoint.getDirection()))
            .filter(point -> point.getTime().isAfter(time))
            .filter(point -> !Duration.between(time, point.getTime()).minus(minLayover).isNegative())
            .findFirst();
    }

    private List<TimePoint> generateTimePoints(LocalTime startTime, LocalTime endTime, Direction direction) {
        val timePoints = new ArrayList<TimePoint>();
        for (LocalTime nextTimePoint = startTime; nextTimePoint.isBefore(endTime); nextTimePoint = nextTimePoint.plus(headway)) {
            timePoints.add(new TimePoint(direction, nextTimePoint,false));
        }
        return timePoints;
    }

}
