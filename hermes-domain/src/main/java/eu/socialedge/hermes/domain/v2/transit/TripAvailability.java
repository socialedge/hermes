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
package eu.socialedge.hermes.domain.v2.transit;

import eu.socialedge.hermes.domain.ext.ValueObject;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Defines a range of dates between which a Trip is available, the
 * days of the week when it is available (such as Monday through Friday).
 *
 * <p>It also may define specific days when a trip is not available,
 * such as holidays.</p>
 */
@ValueObject
public class TripAvailability implements Serializable {

    private final boolean monday;
    private final boolean tuesday;
    private final boolean wednesday;
    private final boolean thursday;
    private final boolean friday;
    private final boolean saturday;
    private final boolean sunday;

    private final LocalDate startDate;
    private final LocalDate endDate;

    private final Set<LocalDate> exceptionDays;

    private TripAvailability(TripAvailabilityBuilder builder) {
        this.monday = builder.monday;
        this.tuesday = builder.tuesday;
        this.wednesday = builder.wednesday;
        this.thursday = builder.thursday;
        this.friday = builder.friday;
        this.saturday = builder.saturday;
        this.sunday = builder.sunday;
        this.startDate = notNull(builder.startDate);
        this.endDate = notNull(builder.endDate);
        this.exceptionDays = builder.exceptionDays;
    }

    public static TripAvailabilityBuilder builder() {
        return new TripAvailabilityBuilder();
    }

    public boolean isOnMondays() {
        return monday;
    }

    public boolean isOnTuesdays() {
        return tuesday;
    }

    public boolean isOnWednesdays() {
        return wednesday;
    }

    public boolean isOnThursdays() {
        return thursday;
    }

    public boolean isOnFridays() {
        return friday;
    }

    public boolean isOnSaturdays() {
        return saturday;
    }

    public boolean isOnSundays() {
        return sunday;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TripAvailability)) return false;
        TripAvailability that = (TripAvailability) o;
        return monday == that.monday &&
                tuesday == that.tuesday &&
                wednesday == that.wednesday &&
                thursday == that.thursday &&
                friday == that.friday &&
                saturday == that.saturday &&
                sunday == that.sunday &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(exceptionDays, that.exceptionDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monday, tuesday, wednesday, thursday, friday,
                saturday, sunday, startDate, endDate, exceptionDays);
    }

    @Override
    public String toString() {
        return "TripAvailability{" +
                "monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", sunday=" + sunday +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", exceptionDays=" + exceptionDays +
                '}';
    }

    public static final TripAvailabilityBuilder WORKING_DAYS = TripAvailability.builder()
            .onMondays()
            .onTuesdays()
            .onWednesdays()
            .onThursdays()
            .onFridays();

    public static final TripAvailabilityBuilder WEEKEND_DAYS = TripAvailability.builder()
            .onSaturdays()
            .onSundays();

    public static TripAvailability workingDays(LocalDate fromDate, LocalDate toDate) {
        return WORKING_DAYS.from(fromDate).to(toDate).build();
    }

    public static TripAvailability weekendDays(LocalDate fromDate, LocalDate toDate) {
        return WEEKEND_DAYS.from(fromDate).to(toDate).build();
    }

    public static class TripAvailabilityBuilder {
        private boolean monday = false;
        private boolean tuesday = false;
        private boolean wednesday = false;
        private boolean thursday = false;
        private boolean friday = false;
        private boolean saturday = false;
        private boolean sunday = false;

        private LocalDate startDate;
        private LocalDate endDate;

        private Set<LocalDate> exceptionDays = new HashSet<>();

        public TripAvailabilityBuilder from(LocalDate fromDate) {
            startDate = fromDate;
            return this;
        }

        public TripAvailabilityBuilder to(LocalDate toDate) {
            endDate = toDate;
            return this;
        }

        public TripAvailabilityBuilder withExceptionDays(LocalDate... exceptionDays) {
            this.exceptionDays = new HashSet<LocalDate>(Arrays.asList(notNull(exceptionDays)));
            return this;
        }

        public TripAvailabilityBuilder withExceptionDays(Collection<LocalDate> exceptionDays) {
            this.exceptionDays = new HashSet<LocalDate>(exceptionDays);
            return this;
        }

        public TripAvailabilityBuilder onMondays() {
            monday = true;
            return this;
        }

        public TripAvailabilityBuilder notOnMondays() {
            monday = false;
            return this;
        }

        public TripAvailabilityBuilder onTuesdays() {
            tuesday = true;
            return this;
        }

        public TripAvailabilityBuilder notOnTuesdays() {
            tuesday = false;
            return this;
        }

        public TripAvailabilityBuilder onWednesdays() {
            wednesday = true;
            return this;
        }

        public TripAvailabilityBuilder notOnWednesdays() {
            wednesday = false;
            return this;
        }

        public TripAvailabilityBuilder onThursdays() {
            thursday = true;
            return this;
        }

        public TripAvailabilityBuilder notOnThursdays() {
            thursday = false;
            return this;
        }

        public TripAvailabilityBuilder onFridays() {
            friday = true;
            return this;
        }

        public TripAvailabilityBuilder notOnFridays() {
            friday = false;
            return this;
        }

        public TripAvailabilityBuilder onSaturdays() {
            saturday = true;
            return this;
        }

        public TripAvailabilityBuilder notOnSaturdays() {
            saturday = false;
            return this;
        }

        public TripAvailabilityBuilder onSundays() {
            sunday = true;
            return this;
        }

        public TripAvailabilityBuilder notOnSundays() {
            sunday = false;
            return this;
        }

        public TripAvailability build() {
            return new TripAvailability(this);
        }
    }
}
