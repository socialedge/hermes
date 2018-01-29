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

import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.gen.TransitConstraints;
import eu.socialedge.hermes.backend.schedule.domain.gen.TripFactory;
import eu.socialedge.hermes.backend.schedule.domain.gen.ScheduleGenerator;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.INBOUND;
import static org.apache.commons.lang3.Validate.notNull;

public class BasicScheduleGenerator implements ScheduleGenerator {

    private final List<Trip> inboundTrips = new ArrayList<>();
    private final List<Trip> outboundTrips = new ArrayList<>();
    private final TripFactory tripFactory;

    public BasicScheduleGenerator(TripFactory tripFactory) {
        this.tripFactory = notNull(tripFactory);
    }

    @Override
    public Schedule generate(Line line, Availability availability, String description, TransitConstraints transitConstraints) {
        val timePoints = new ScheduleTimePoints(transitConstraints);
        Optional<TimePoint> startPointOpt = timePoints.findFirstNotServicedTimePoint();
        while (startPointOpt.isPresent()) {
            generateVehicleTrips(timePoints, startPointOpt.get(), line);
            startPointOpt = timePoints.findFirstNotServicedTimePoint();
        }
        return new Schedule(description, availability, line, inboundTrips, outboundTrips);
    }

    private void generateVehicleTrips(ScheduleTimePoints timePoints, TimePoint startPoint, Line line) {
        //TODO maybe remove last trip and break if its arrival time is after end time? May be some parameter to indicate possible lateness?
        Optional<TimePoint> nextPointOpt = Optional.ofNullable(startPoint);
        while (nextPointOpt.isPresent()) {
            val currentPoint = nextPointOpt.get();
            val trip = tripFactory.create(currentPoint.getTime(), getRoute(currentPoint.getDirection(), line));
            addTrip(currentPoint.getDirection(), trip);
            nextPointOpt = timePoints.findNextNotServicedTimePointAfter(trip.getArrivalTime(), currentPoint);
            currentPoint.markServiced();
        }
    }

    private void addTrip(Direction direction, Trip trip) {
        if (INBOUND.equals(direction)) {
            inboundTrips.add(trip);
        } else {
            outboundTrips.add(trip);
        }
    }

    private Route getRoute(Direction direction, Line line) {
        return INBOUND.equals(direction) ? line.getInboundRoute() : line.getOutboundRoute();
    }
}
