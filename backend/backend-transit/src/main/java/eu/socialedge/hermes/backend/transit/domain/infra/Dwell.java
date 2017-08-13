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

package eu.socialedge.hermes.backend.transit.domain.infra;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalTime;

/**
 * {@code Dwell} describes a time a vehicle may spend at the {@link Station}
 * to pick up or drop off passengers in defined time frame {@link Dwell#from}
 * - {@link Dwell#to}.
 */
@Document
@ToString @EqualsAndHashCode
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Dwell {

    @Getter
    private final Duration dwellTime;

    @Getter
    private final LocalTime from;

    @Getter
    private final LocalTime to;

    @Getter
    private final boolean hail;

    protected Dwell(LocalTime from, LocalTime to, Duration dwellTime, boolean hail) {
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

        this.from = from;
        this.to = to;
        this.dwellTime = dwellTime;
        this.hail = hail;
    }

    public static Dwell hail(LocalTime from, LocalTime to, Duration dwellTime) {
        return new Dwell(from, to, dwellTime, true);
    }

    public static Dwell allDayHail(Duration duration) {
        return new Dwell(LocalTime.MIN, LocalTime.MAX, duration, true);
    }

    public static Dwell regular(LocalTime from, LocalTime to, Duration dwellTime) {
        return new Dwell(from, to, dwellTime, false);
    }

    public static Dwell allDayRegular(Duration duration) {
        return new Dwell(LocalTime.MIN, LocalTime.MAX, duration, true);
    }

    /**
     * Checks if period of validity [from; to] of this {@code Dwell} overlaps
     * with period of validity of other {@code Dwell}.
     * <p>
     * Detects HEADtail, TAILhead, THISinclusive and thatINCLUSIVE overlapping:
     * <ol>
     *     <li><strong>HEADtail</strong>
     *     <pre>
     *     this  F ------ T
     *     that       F ------ T
     *     </pre>
     *     </li>
     *
     *     <li><strong>TAILhead</strong>
     *     <pre>
     *     this       F ------ T
     *     that  F ------ T
     *     </pre>
     *     </li>
     *
     *     <li><strong>THISinclusive</strong>
     *     <pre>
     *     this  F ---------- T
     *     that     F ---- T
     *     </pre>
     *     </li>
     *
     *     <li><strong>thatINCLUSIVE</strong>
     *     <pre>
     *     this     F ---- T
     *     that  F ---------- T
     *     </pre>
     *     </li>
     * </ol>
     *
     * @param that other {@code Dwell} to detect overlapping against
     * @return true if period of validity [from; to] of this {@code Dwell} overlaps
     * with period of validity of other {@code Dwell}
     */
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
                        // this     F ---- T
                        // that  F ---------- T
                        || (this.from.isAfter(that.from) && this.to.isBefore(that.to));
    }

    /**
     * Checks whether given time lies inside a period of validity of
     * this {@code Dwell}.
     *
     * @param time a time to check
     * @return true if given time lies inside a period of validity
     */
    public boolean applies(LocalTime time) {
        return from.equals(time) || to.equals(time) || from.isBefore(time) && to.isAfter(time);
    }
}
