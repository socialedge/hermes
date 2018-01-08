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

import eu.socialedge.hermes.backend.schedule.domain.Availability;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.schedule.domain.gen.ScheduleGenerator;
import eu.socialedge.hermes.backend.schedule.domain.gen.StopsGenerator;
import eu.socialedge.hermes.backend.transit.domain.service.Line;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import lombok.*;

import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.INBOUND;
import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.Direction.OUTBOUND;

@Builder
@Setter
public class BasicScheduleGenerator implements ScheduleGenerator {

    private String description;
    private @NonNull Availability availability;

    private @NonNull Line line;

    private @NonNull LocalTime startTimeInbound;
    private @NonNull LocalTime endTimeInbound;

    private @NonNull LocalTime startTimeOutbound;
    private @NonNull LocalTime endTimeOutbound;

    private @NonNull Duration headway;
    private @NonNull Quantity<Speed> averageSpeed;
    private @NonNull Duration minLayover;

    private final List<Trip> inboundTrips = new ArrayList<>();
    private final List<Trip> outboundTrips = new ArrayList<>();
    private ScheduleTimePoints timePoints;

    @Override
    public Schedule generate() {
        timePoints = new ScheduleTimePoints(generateTimePoints(), minLayover);
        generateTrips();
        return new Schedule(description, availability, line, inboundTrips, outboundTrips);
    }

    private ArrayList<TimePoint> generateTimePoints() {
        val timePoints = new ArrayList<TimePoint>();
        for (LocalTime nextTimePoint = startTimeInbound; nextTimePoint.isBefore(endTimeInbound); nextTimePoint = nextTimePoint.plus(headway)) {
            timePoints.add(new TimePoint(INBOUND, nextTimePoint,false));
        }
        for (LocalTime nextTimePoint = startTimeOutbound; nextTimePoint.isBefore(endTimeOutbound); nextTimePoint = nextTimePoint.plus(headway)) {
            timePoints.add(new TimePoint(OUTBOUND, nextTimePoint,false));
        }
        timePoints.sort(Comparator.comparing(TimePoint::getTime));
        return timePoints;
    }

    private void generateTrips() {
        Optional<TimePoint> startPointOpt = timePoints.findNextNotServicedTimePoint();
        for (int vehId = 1; startPointOpt.isPresent(); vehId++, startPointOpt = timePoints.findNextNotServicedTimePoint()) {
            generateVehicleTrips(vehId, startPointOpt.get());
        }
    }

    private void generateVehicleTrips(int vehicleId, TimePoint startPoint) {
        Optional<TimePoint> nextPointOpt = Optional.ofNullable(startPoint);
        while (nextPointOpt.isPresent()) {
            val currentPoint = nextPointOpt.get();
            val route = INBOUND.equals(currentPoint.getDirection()) ? line.getInboundRoute() : line.getOutboundRoute();
            val trip = generateTrip(vehicleId, route, currentPoint.getTime());
            if (INBOUND.equals(currentPoint.getDirection())) {
                inboundTrips.add(trip);
            } else {
                outboundTrips.add(trip);
            }
            currentPoint.markServiced();

            //TODO maybe remove last trip and break if its arrival time is after end time? May be some parameter to indicate possible lateness?
            nextPointOpt = timePoints.findNextNotServicedTimePointAfter(trip.getArrivalTime(), currentPoint.getDirection().oppositeDirection());
        }
    }

    private Trip generateTrip(int vehicleId, Route route, LocalTime startTime) {
        return new Trip(vehicleId, StopsGenerator.generate(startTime, route, averageSpeed));
    }

}
