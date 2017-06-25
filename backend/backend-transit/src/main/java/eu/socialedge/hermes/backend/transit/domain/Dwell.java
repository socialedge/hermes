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

package eu.socialedge.hermes.backend.transit.domain;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalTime;

/**
 * {@code Dwell} describes a time a vehicle may spend at the {@link Station}
 * to pick up or drop off passengers with certain {@link Dwell#probability}
 * in defined time frame {@link Dwell#from} - {@link Dwell#to}.
 *
 */
@Document
@ToString @EqualsAndHashCode
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Dwell {

    private static final double REGULAR_PROBABILITY = 1;
    private static final double EQUALLY_LIKELY_PROBABILITY = 0.5;

    @Getter
    private final double probability;

    @Getter
    private final Duration dwellTime;

    @Getter
    private final LocalTime from;

    @Getter
    private final LocalTime to;

    protected Dwell(LocalTime from, LocalTime to, Duration dwellTime, double probability) {
        if (from.isAfter(to))
            throw new IllegalArgumentException("From time cannot be after to");
        else if (from.equals(to))
            throw new IllegalArgumentException("To time must be before From, not equals");
        else if (dwellTime.isNegative())
            throw new IllegalArgumentException("Duration can't be negative");
        else if (dwellTime.isZero())
            throw new IllegalArgumentException("What's the point of zero-timed dwell, bro?");
        else if (Duration.between(from, to).minus(dwellTime).isNegative())
            throw new IllegalArgumentException("Dwell time can't be bigger than dwell validity interval [from, to]");

        if (probability < 0 || probability > 1)
            throw new IllegalArgumentException("Hail probability must be [0...1]");

        this.from = from;
        this.to = to;
        this.dwellTime = dwellTime;
        this.probability = probability;
    }

    public static Dwell regular(LocalTime from, LocalTime to, Duration duration) {
        return new Dwell(from, to, duration, REGULAR_PROBABILITY);
    }

    public static Dwell allDayRegular(Duration duration) {
        return new Dwell(LocalTime.MIN, LocalTime.MAX, duration, REGULAR_PROBABILITY);
    }

    public static Dwell hail(LocalTime from, LocalTime to, Duration duration, double probability) {
        return new Dwell(from, to, duration, probability);
    }

    public static Dwell allDayHail(Duration duration, double probability) {
        return new Dwell(LocalTime.MIN, LocalTime.MAX, duration, probability);
    }

    public static Dwell equallyLikely(LocalTime from, LocalTime to, Duration duration) {
        return new Dwell(from, to, duration, EQUALLY_LIKELY_PROBABILITY);
    }

    public static Dwell allDayEquallyLikely(Duration duration) {
        return new Dwell(LocalTime.MIN, LocalTime.MAX, duration, EQUALLY_LIKELY_PROBABILITY);
    }

    public boolean isHail() {
        return probability < 1;
    }

    public boolean isRegular() {
        return probability == 1;
    }

    public boolean overlaps(Dwell that) {
            // this  F ------ T
            // that       F ------ T
        return (this.from.isBefore(that.from) && this.to.isAfter(that.from))
                // this       F ------ T
                // that F ------ T
                || (this.from.isAfter(that.from) && this.from.isBefore(that.to))
                    // this  F ---------- T
                    // that     F ---- T
                    || (this.from.isBefore(that.from) && this.to.isAfter(that.to))
                        // this    F ---- T
                        // that F ---------- T
                        || (this.from.isAfter(that.from) && this.to.isBefore(that.to));
    }

    public static void main(String[] a) {
        System.out.println(Duration.ofSeconds(20).toString());
    }
}
