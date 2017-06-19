/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2017 SocialEdge
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
package eu.socialedge.hermes.backend.schedule.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Defines a range of dates between which the {@link Schedule} is available
 * and the days of the week when it is available (such as Monday through Friday).
 *
 * <p>It also may define specific days when a trip is not available,
 * such as holidays.</p>
 */
@Document
@EqualsAndHashCode @ToString
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Availability implements Serializable {

    private final Set<DayOfWeek> weekDays;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final Set<LocalDate> exceptionDays;

    private Availability(ScheduleAvailabilityBuilder builder) {
        this.weekDays = notEmpty(builder.availabilityDays,
            "At least one availability day of the week must be specified");
        this.startDate = notNull(builder.startDate);
        this.endDate = notNull(builder.endDate);
        this.exceptionDays = builder.exceptionDays;
    }

    public boolean isOnMondays() {
        return weekDays.contains(DayOfWeek.MONDAY);
    }

    public boolean isOnTuesdays() {
        return weekDays.contains(DayOfWeek.TUESDAY);
    }

    public boolean isOnWednesdays() {
        return weekDays.contains(DayOfWeek.WEDNESDAY);
    }

    public boolean isOnThursdays() {
        return weekDays.contains(DayOfWeek.THURSDAY);
    }

    public boolean isOnFridays() {
        return weekDays.contains(DayOfWeek.FRIDAY);
    }

    public boolean isOnSaturdays() {
        return weekDays.contains(DayOfWeek.SATURDAY);
    }

    public boolean isOnSundays() {
        return weekDays.contains(DayOfWeek.SUNDAY);
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public Set<LocalDate> exceptionDays() {
        return exceptionDays;
    }

    public Set<DayOfWeek> availabilityDays() {
        return Collections.unmodifiableSet(weekDays);
    }

    public static ScheduleAvailabilityBuilder builder() {
        return new ScheduleAvailabilityBuilder();
    }

    public static class ScheduleAvailabilityBuilder {
        private Set<DayOfWeek> availabilityDays = new HashSet<>();

        private LocalDate startDate;
        private LocalDate endDate;

        private Set<LocalDate> exceptionDays = new HashSet<>();

        public ScheduleAvailabilityBuilder from(LocalDate fromDate) {
            startDate = fromDate;
            return this;
        }

        public ScheduleAvailabilityBuilder to(LocalDate toDate) {
            endDate = toDate;
            return this;
        }

        public ScheduleAvailabilityBuilder withExceptionDays(LocalDate... exceptionDays) {
            this.exceptionDays = new HashSet<>(Arrays.asList(notNull(exceptionDays)));
            return this;
        }

        public ScheduleAvailabilityBuilder withExceptionDays(Collection<LocalDate> exceptionDays) {
            this.exceptionDays = new HashSet<>(exceptionDays);
            return this;
        }

        public ScheduleAvailabilityBuilder onMondays() {
            availabilityDays.add(DayOfWeek.MONDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder notOnMondays() {
            availabilityDays.remove(DayOfWeek.MONDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder onTuesdays() {
            availabilityDays.add(DayOfWeek.TUESDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder notOnTuesdays() {
            availabilityDays.remove(DayOfWeek.TUESDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder onWednesdays() {
            availabilityDays.add(DayOfWeek.WEDNESDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder notOnWednesdays() {
            availabilityDays.remove(DayOfWeek.WEDNESDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder onThursdays() {
            availabilityDays.add(DayOfWeek.THURSDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder notOnThursdays() {
            availabilityDays.remove(DayOfWeek.THURSDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder onFridays() {
            availabilityDays.add(DayOfWeek.FRIDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder notOnFridays() {
            availabilityDays.remove(DayOfWeek.FRIDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder onSaturdays() {
            availabilityDays.add(DayOfWeek.SATURDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder notOnSaturdays() {
            availabilityDays.remove(DayOfWeek.SATURDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder onSundays() {
            availabilityDays.add(DayOfWeek.SUNDAY);
            return this;
        }

        public ScheduleAvailabilityBuilder notOnSundays() {
            availabilityDays.remove(DayOfWeek.SUNDAY);
            return this;
        }

        public Availability build() {
            return new Availability(this);
        }
    }

    public static Availability workingDays(LocalDate fromDate, LocalDate toDate) {
        return Availability.builder()
                           .from(fromDate).to(toDate)
                           .onMondays()
                           .onTuesdays()
                           .onWednesdays()
                           .onThursdays()
                           .onFridays()
                           .build();
    }

    public static Availability weekendDays(LocalDate fromDate, LocalDate toDate) {
        return Availability.builder()
                           .from(fromDate).to(toDate)
                           .onSaturdays()
                           .onSundays()
                           .build();
    }
}
