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

import eu.socialedge.hermes.backend.schedule.domain.gen.TripFactory;
import eu.socialedge.hermes.backend.schedule.domain.gen.TripsGenerator;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import lombok.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.INBOUND;

@Builder
@Setter
public class BasicTripsGenerator implements TripsGenerator {

    private @NonNull TripFactory tripFactory;

    private @NonNull Route inboundRoute;
    private @NonNull Route outboundRoute;

    private @NonNull LocalTime startTimeInbound;
    private @NonNull LocalTime endTimeInbound;

    private @NonNull LocalTime startTimeOutbound;
    private @NonNull LocalTime endTimeOutbound;

    private @NonNull Duration headway;
    private @NonNull Duration minLayover;

    private final List<Trip> inboundTrips = new ArrayList<>();
    private final List<Trip> outboundTrips = new ArrayList<>();

    @Override
    public void generate() {
        val timePoints = new ScheduleTimePoints(startTimeInbound, startTimeOutbound, endTimeInbound, endTimeOutbound, minLayover, headway);
        Optional<TimePoint> startPointOpt = timePoints.findFirstNotServicedTimePoint();
        for (int vehId = 1; startPointOpt.isPresent(); vehId++, startPointOpt = timePoints.findFirstNotServicedTimePoint()) {
            generateVehicleTrips(vehId, timePoints, startPointOpt.get());
        }
    }

    private void generateVehicleTrips(int vehicleId, ScheduleTimePoints timePoints, TimePoint startPoint) {
        //TODO maybe remove last trip and break if its arrival time is after end time? May be some parameter to indicate possible lateness?
        Optional<TimePoint> nextPointOpt = Optional.ofNullable(startPoint);
        while (nextPointOpt.isPresent()) {
            val currentPoint = nextPointOpt.get();
            val trip = tripFactory.create(currentPoint.getTime(), vehicleId, getRoute(currentPoint.getDirection()));
            addTrip(currentPoint.getDirection(), trip);
            nextPointOpt = timePoints.findNextNotServicedTimePointAfter(trip.getArrivalTime(), currentPoint);
            currentPoint.markServiced();
        }
    }

    public List<Trip> getInboundTrips() {
        return Collections.unmodifiableList(inboundTrips);
    }

    public List<Trip> getOutboundTrips() {
        return Collections.unmodifiableList(outboundTrips);
    }

    private void addTrip(Direction direction, Trip trip) {
        if (INBOUND.equals(direction)) {
            inboundTrips.add(trip);
        } else {
            outboundTrips.add(trip);
        }
    }

    private Route getRoute(Direction direction) {
        return INBOUND.equals(direction) ? inboundRoute : outboundRoute;
    }
}
