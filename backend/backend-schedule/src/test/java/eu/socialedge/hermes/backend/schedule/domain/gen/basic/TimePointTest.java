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

import lombok.val;
import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.assertTrue;

public class TimePointTest {

    @Test
    public void shouldMakePointServiced() {
        val timePoint = new TimePoint(Direction.INBOUND, LocalTime.now(), false);

        timePoint.markServiced();

        assertTrue(timePoint.isServiced());
    }

    @Test
    public void shouldRetainPointServicedIfItWasAlready() {
        val timePoint = new TimePoint(Direction.INBOUND, LocalTime.now(), true);

        timePoint.markServiced();

        assertTrue(timePoint.isServiced());
    }
}
