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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.mapping;

import eu.socialedge.hermes.domain.v2.transit.TripAvailability;
import eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity.JpaTripAvailability;
import org.springframework.stereotype.Component;

@Component
public class TripAvailabilityEntityMapper implements EntityMapper<TripAvailability, JpaTripAvailability> {
    
    @Override
    public JpaTripAvailability mapToEntity(TripAvailability availability) {
        JpaTripAvailability jpaTripAvailability = new JpaTripAvailability();

        if (availability.isOnMondays()) jpaTripAvailability.onMondays();
        if (availability.isOnTuesdays()) jpaTripAvailability.onTuesdays();
        if (availability.isOnWednesdays()) jpaTripAvailability.onWednesdays();
        if (availability.isOnThursdays()) jpaTripAvailability.onThursdays();
        if (availability.isOnFridays()) jpaTripAvailability.onFridays();
        if (availability.isOnSaturdays()) jpaTripAvailability.onSaturdays();
        if (availability.isOnSundays()) jpaTripAvailability.onSundays();

        jpaTripAvailability.startDate(availability.startDate());
        jpaTripAvailability.endDate(availability.endDate());
        jpaTripAvailability.exceptionDays(availability.exceptionDays());

        return jpaTripAvailability;
    }

    @Override
    public TripAvailability mapToDomain(JpaTripAvailability jpaAvailability) {
        TripAvailability.TripAvailabilityBuilder builder = TripAvailability.builder();

        if (jpaAvailability.isOnMondays()) builder.onMondays();
        if (jpaAvailability.isOnTuesdays()) builder.onTuesdays();
        if (jpaAvailability.isOnWednesdays()) builder.onWednesdays();
        if (jpaAvailability.isOnThursdays()) builder.onThursdays();
        if (jpaAvailability.isOnFridays()) builder.onFridays();
        if (jpaAvailability.isOnSaturdays()) builder.onSaturdays();
        if (jpaAvailability.isOnSundays()) builder.onSundays();

        builder.from(jpaAvailability.startDate());
        builder.to(jpaAvailability.endDate());
        builder.withExceptionDays(jpaAvailability.exceptionDays());

        return builder.build();
    }
}
