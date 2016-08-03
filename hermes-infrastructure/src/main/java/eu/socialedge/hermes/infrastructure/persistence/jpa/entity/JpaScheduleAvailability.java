/**
 * Hermes - The Municipal Transport Timetable System Copyright (c) 2016 SocialEdge <p> This program
 * is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. <p> This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */
package eu.socialedge.hermes.infrastructure.persistence.jpa.entity;

import eu.socialedge.hermes.infrastructure.persistence.jpa.entity.convert.LocalDateSetToStringConverter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

@Embeddable
public class JpaScheduleAvailability implements Serializable {

    @Column(name = "availability_monday", nullable = false)
    private boolean monday = false;

    @Column(name = "availability_tuesday", nullable = false)
    private boolean tuesday = false;

    @Column(name = "availability_wednesday", nullable = false)
    private boolean wednesday = false;

    @Column(name = "availability_thursday", nullable = false)
    private boolean thursday = false;

    @Column(name = "availability_friday", nullable = false)
    private boolean friday = false;

    @Column(name = "availability_saturday", nullable = false)
    private boolean saturday = false;

    @Column(name = "availability_sunday", nullable = false)
    private boolean sunday = false;

    @Column(name = "availability_start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "availability_end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "availability_exception_days", nullable = false)
    @Convert(converter = LocalDateSetToStringConverter.class)
    private Set<LocalDate> exceptionDays = new HashSet<>();

    public JpaScheduleAvailability() {}

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

    public void startDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void endDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void exceptionDays(Set<LocalDate> exceptionDays) {
        this.exceptionDays = exceptionDays;
    }

    public Set<LocalDate> exceptionDays() {
        return exceptionDays;
    }

    public void onMondays() {
        monday = true;
    }

    public void notOnMondays() {
        monday = false;
    }

    public void onTuesdays() {
        tuesday = true;
    }

    public void notOnTuesdays() {
        tuesday = false;
    }

    public void onWednesdays() {
        wednesday = true;
    }

    public void notOnWednesdays() {
        wednesday = false;
    }

    public void onThursdays() {
        thursday = true;
    }

    public void notOnThursdays() {
        thursday = false;
    }

    public void onFridays() {
        friday = true;
    }

    public void notOnFridays() {
        friday = false;
    }

    public void onSaturdays() {
        saturday = true;
    }

    public void notOnSaturdays() {
        saturday = false;
    }

    public void onSundays() {
        sunday = true;
    }

    public void notOnSundays() {
        sunday = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JpaScheduleAvailability)) return false;
        JpaScheduleAvailability that = (JpaScheduleAvailability) o;
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
        return Objects.hash(monday, tuesday, wednesday, thursday, friday, saturday, sunday, startDate, endDate, exceptionDays);
    }
}
