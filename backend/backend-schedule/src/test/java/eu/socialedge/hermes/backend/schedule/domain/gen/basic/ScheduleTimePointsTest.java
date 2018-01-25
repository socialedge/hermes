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

import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.INBOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScheduleTimePointsTest {
    private ScheduleTimePoints timePoints;

    @Test
    public void shouldReturnEarliestTimePoint() {
        LocalTime startTime = LocalTime.now();
        timePoints = new ScheduleTimePoints(startTime, startTime, startTime.plusHours(2), startTime.plusHours(2), Duration.ofMinutes(20));

        Optional<TimePoint> timePointOpt = timePoints.findFirstNotServicedTimePoint();

        assertNotNull(timePointOpt);
        assertTrue(timePointOpt.isPresent());
        assertFalse(timePointOpt.get().isServiced());
        assertEquals(timePointOpt.get().getTime(), startTime);
    }

    @Test
    public void shouldNotReturnServicedTimePoints() {
        LocalTime startTimeInbound = LocalTime.now();
        LocalTime startTimeOutbound = LocalTime.now().plusMinutes(1);
        timePoints = new ScheduleTimePoints(startTimeInbound, startTimeOutbound, startTimeInbound.plusHours(2),
            startTimeOutbound.plusHours(2), Duration.ofMinutes(20));

        Optional<TimePoint> firstTimePointOpt = timePoints.findFirstNotServicedTimePoint();
        firstTimePointOpt.get().markServiced();
        Optional<TimePoint> nextTimePointOpt = timePoints.findFirstNotServicedTimePoint();

        assertNotNull(nextTimePointOpt);
        assertTrue(nextTimePointOpt.isPresent());
        assertFalse(nextTimePointOpt.get().isServiced());
        assertNotEquals(firstTimePointOpt.get(), nextTimePointOpt.get());
        assertEquals(nextTimePointOpt.get().getTime(), INBOUND.equals(nextTimePointOpt.get().getDirection()) ? startTimeInbound : startTimeOutbound);
    }

    @Test
    public void shouldGenerateAllTimePointsThroughTimePeriod() {
        LocalTime startTime = LocalTime.now();
        LocalTime endTime = startTime.plusHours(2);
        Duration headway = Duration.ofMinutes(20);
        timePoints = new ScheduleTimePoints(startTime, startTime, endTime, endTime, headway);

        Duration timePeriod = Duration.between(endTime, startTime);

    }
}
