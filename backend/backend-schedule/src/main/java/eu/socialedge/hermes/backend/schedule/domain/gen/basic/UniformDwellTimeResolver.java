/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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

import eu.socialedge.hermes.backend.transit.domain.Station;
import lombok.val;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;

public class UniformDwellTimeResolver implements DwellTimeResolver {

    @Override
    public Optional<Duration> resolve(LocalTime arrival, Station station) {
        val applicableDwellOpt = station.getDwells().stream()
            .filter(d -> d.applies(arrival))
            .findAny();

        if (!applicableDwellOpt.isPresent())
            return Optional.empty();


        val applicableDwell = applicableDwellOpt.get();
        val dwellTime = calculateDwellTimeUniformly(applicableDwell.getDwellTime(), applicableDwell.getProbability());

        return Optional.of(dwellTime);
    }

    private static Duration calculateDwellTimeUniformly(Duration duration, double probability) {
        val seconds = duration.getSeconds();
        val unifSeconds = Math.ceil(seconds * probability);

        return Duration.ofSeconds((long) unifSeconds);
    }

    public static void main(String[] a) {
        System.out.println(calculateDwellTimeUniformly(Duration.ofSeconds(20), 0.5).toString());
    }

}
