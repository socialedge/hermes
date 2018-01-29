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
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.INBOUND;
import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.OUTBOUND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ScheduleTimePointsTest {
    private ScheduleTimePoints timePoints;

    @Test
    public void shouldReturnEarliestTimePoint() {
        val startTime = LocalTime.now();
        timePoints = new ScheduleTimePoints(new TransitConstraints(startTime, startTime.plusHours(2), startTime, startTime.plusHours(2), Duration.ofMinutes(20), Duration.ofMinutes(20)));

        val timePointOpt = timePoints.findFirstNotServicedTimePoint();

        assertNotNull(timePointOpt);
        assertTrue(timePointOpt.isPresent());
        assertFalse(timePointOpt.get().isServiced());
        assertEquals(timePointOpt.get().getTime(), startTime);
    }

    @Test
    public void shouldNotReturnServicedTimePoints() {
        val startTimeInbound = LocalTime.now();
        val startTimeOutbound = LocalTime.now().plusMinutes(1);
        timePoints = new ScheduleTimePoints(new TransitConstraints(startTimeInbound, startTimeInbound.plusHours(2), startTimeOutbound,
            startTimeOutbound.plusHours(2), Duration.ofMinutes(20), Duration.ofMinutes(20)));

        val firstTimePointOpt = timePoints.findFirstNotServicedTimePoint();
        firstTimePointOpt.get().markServiced();
        val nextTimePointOpt = timePoints.findFirstNotServicedTimePoint();

        assertNotNull(nextTimePointOpt);
        assertTrue(nextTimePointOpt.isPresent());
        assertFalse(nextTimePointOpt.get().isServiced());
        assertNotEquals(firstTimePointOpt.get(), nextTimePointOpt.get());
        assertEquals(nextTimePointOpt.get().getTime(), INBOUND.equals(nextTimePointOpt.get().getDirection()) ? startTimeInbound : startTimeOutbound);
    }

    @Test
    public void shouldGenerateAllTimePointsThroughTimePeriod() {
        val startTime = LocalTime.now();
        val endTime = startTime.plusHours(2);
        val headway = Duration.ofMinutes(20);
        val scheduleTimePoints = new ScheduleTimePoints(new TransitConstraints(startTime, endTime, startTime, endTime, headway, headway));

        val timePoints = collectAllTimePoints(scheduleTimePoints);
        for (int i = 0; i < timePoints.get(INBOUND).size() - 1; i++) {
            val currentTimePoint = timePoints.get(INBOUND).get(i);
            val nextTimePoint = timePoints.get(INBOUND).get(i + 1);
            assertEquals(headway, Duration.between(currentTimePoint.getTime(), nextTimePoint.getTime()));
        }
        for (int i = 0; i < timePoints.get(OUTBOUND).size() - 1; i++) {
            val currentTimePoint = timePoints.get(OUTBOUND).get(i);
            val nextTimePoint = timePoints.get(OUTBOUND).get(i + 1);
            assertEquals(headway, Duration.between(currentTimePoint.getTime(), nextTimePoint.getTime()));
        }
    }

    @Test
    public void shouldReturnNextTimePointWithOppositeDirectionAfterSpecifiedTime() {
        val startTime = LocalTime.now();
        val headway = Duration.ofMinutes(20);
        val minLayover = Duration.ofMinutes(2);
        timePoints = new ScheduleTimePoints(new TransitConstraints(startTime, startTime.plusHours(2), startTime,
            startTime.plusHours(2), headway, minLayover));

        val firstTimePoint = timePoints.findFirstNotServicedTimePoint().get();
        val nextTimePointOpt = timePoints.findNextNotServicedTimePointAfter(startTime.plus(headway).minus(minLayover.plusMinutes(1)), firstTimePoint);

        assertTrue(nextTimePointOpt.isPresent());
        val nextTimePoint = nextTimePointOpt.get();
        assertNotEquals(firstTimePoint.getDirection(), nextTimePoint.getDirection());
        assertEquals(firstTimePoint.getTime().plus(headway), nextTimePoint.getTime());
        assertFalse(nextTimePoint.isServiced());
    }

    @Test
    public void shouldReturnNextTimePointWithOppositeDirectionAfterSpecifiedTimeWithRegardToMinLayover() {
        val startTime = LocalTime.now();
        val headway = Duration.ofMinutes(20);
        val minLayover = Duration.ofMinutes(2);
        timePoints = new ScheduleTimePoints(new TransitConstraints(startTime, startTime.plusHours(2), startTime,
            startTime.plusHours(2), headway, minLayover));

        val firstTimePoint = timePoints.findFirstNotServicedTimePoint().get();
        val nextTimePointOpt = timePoints.findNextNotServicedTimePointAfter(startTime.plus(headway).minus(minLayover.minusMinutes(1)), firstTimePoint);

        assertTrue(nextTimePointOpt.isPresent());
        val nextTimePoint = nextTimePointOpt.get();
        assertNotEquals(firstTimePoint.getDirection(), nextTimePoint.getDirection());
        assertEquals(firstTimePoint.getTime().plus(headway.multipliedBy(2)), nextTimePoint.getTime());
        assertFalse(nextTimePoint.isServiced());
    }

    private Map<Direction, List<TimePoint>> collectAllTimePoints(ScheduleTimePoints scheduleTimePoints) {
        val timePoints = new HashMap<Direction, List<TimePoint>>();
        timePoints.put(INBOUND, new ArrayList<>());
        timePoints.put(OUTBOUND, new ArrayList<>());
        while(true) {
            val timePointOpt = scheduleTimePoints.findFirstNotServicedTimePoint();
            if (timePointOpt.isPresent()) {
                TimePoint timePoint = timePointOpt.get();
                timePoints.get(timePoint.getDirection()).add(timePoint);
                timePoint.markServiced();
            } else {
                break;
            }
        }
        return timePoints;
    }
}
