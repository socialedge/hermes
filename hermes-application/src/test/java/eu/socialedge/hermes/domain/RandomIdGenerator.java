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
package eu.socialedge.hermes.domain;

import java.util.concurrent.ThreadLocalRandom;

public class RandomIdGenerator {
    public static String randomAgencyId() {
        return "ag" + randomString();
    }

    public static String randomLineId() {
        return "ln" + randomString();
    }

    public static String randomRouteId() {
        return "rt" + randomString();
    }

    public static String randomScheduleId() {
        return "sch" + randomString();
    }

    public static String randomStationId() {
        return "st" + randomString();
    }

    public static String randomTripId() {
        return "tr" + randomString();
    }

    private static String randomString() {
        return String.valueOf(System.nanoTime())
                + String.valueOf(ThreadLocalRandom.current().nextInt(1, 100));
    }
}
