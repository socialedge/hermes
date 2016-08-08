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
package eu.socialedge.hermes.domain.timetable;

import eu.socialedge.hermes.domain.ext.ValueObject;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static eu.socialedge.hermes.domain.shared.util.Iterables.requireNotEmpty;
import static eu.socialedge.hermes.domain.shared.util.Values.requireNotNull;

/**
 * Defines a range of dates between which the {@link Schedule} is available
 * and the days of the week when it is available (such as Monday through Friday).
 *
 * <p>It also may define specific days when a trip is not available,
 * such as holidays.</p>
 */
@ValueObject
public class ScheduleAvailability implements Serializable {

    private final Set<DayOfWeek> availabilityDays;

    private final LocalDate startDate;
    private final LocalDate endDate;

    private final Set<LocalDate> exceptionDays;

    private ScheduleAvailability(ScheduleAvailabilityBuilder builder) {
        this.availabilityDays = requireNotEmpty(builder.availabilityDays,
                "At least one availability day of the week must be specified");
        this.startDate = requireNotNull(builder.startDate);
        this.endDate = requireNotNull(builder.endDate);
        this.exceptionDays = builder.exceptionDays;
    }

    public static ScheduleAvailabilityBuilder builder() {
        return new ScheduleAvailabilityBuilder();
    }

    public boolean isOnMondays() {
        return availabilityDays.contains(DayOfWeek.MONDAY);
    }

    public boolean isOnTuesdays() {
        return availabilityDays.contains(DayOfWeek.TUESDAY);
    }

    public boolean isOnWednesdays() {
        return availabilityDays.contains(DayOfWeek.WEDNESDAY);
    }

    public boolean isOnThursdays() {
        return availabilityDays.contains(DayOfWeek.THURSDAY);
    }

    public boolean isOnFridays() {
        return availabilityDays.contains(DayOfWeek.FRIDAY);
    }

    public boolean isOnSaturdays() {
        return availabilityDays.contains(DayOfWeek.SATURDAY);
    }

    public boolean isOnSundays() {
        return availabilityDays.contains(DayOfWeek.SUNDAY);
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
        return Collections.unmodifiableSet(availabilityDays);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleAvailability)) return false;
        ScheduleAvailability that = (ScheduleAvailability) o;
        return Objects.equals(availabilityDays, that.availabilityDays) &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(exceptionDays, that.exceptionDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(availabilityDays, startDate, endDate, exceptionDays);
    }

    @Override
    public String toString() {
        return "ScheduleAvailability{" +
                "availabilityDays=" + availabilityDays +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", exceptionDays=" + exceptionDays +
                '}';
    }

    public static final ScheduleAvailabilityBuilder WORKING_DAYS = ScheduleAvailability.builder()
            .onMondays()
            .onTuesdays()
            .onWednesdays()
            .onThursdays()
            .onFridays();

    public static final ScheduleAvailabilityBuilder WEEKEND_DAYS = ScheduleAvailability.builder()
            .onSaturdays()
            .onSundays();

    public static ScheduleAvailability workingDays(LocalDate fromDate, LocalDate toDate) {
        return WORKING_DAYS.from(fromDate).to(toDate).build();
    }

    public static ScheduleAvailability weekendDays(LocalDate fromDate, LocalDate toDate) {
        return WEEKEND_DAYS.from(fromDate).to(toDate).build();
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
            this.exceptionDays = new HashSet<>(Arrays.asList(requireNotNull(exceptionDays)));
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
            availabilityDays.remove(DayOfWeek.MONDAY);
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

        public ScheduleAvailability build() {
            return new ScheduleAvailability(this);
        }
    }
}
