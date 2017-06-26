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
import eu.socialedge.hermes.backend.schedule.domain.gen.ScheduleGeneratorException;
import eu.socialedge.hermes.backend.transit.domain.Line;
import eu.socialedge.hermes.backend.transit.domain.Route;
import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import lombok.*;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.BasicScheduleGenerator.Direction.INBOUND;
import static eu.socialedge.hermes.backend.schedule.domain.gen.basic.BasicScheduleGenerator.Direction.OUTBOUND;

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

    private DwellTimeResolver dwellTimeResolver;

    @Builder.Default
    private boolean failFast = true;

    @Override
    public Schedule generate() {
        return new Schedule(description, availability, line, generateTrips());
    }

    private List<Trip> generateTrips() {
        val timePoints = generateTimePoints();
        timePoints.sort(Comparator.comparing(TimePoint::getTime));

        val trips = new ArrayList<Trip>();
        for (int vehId = 1; hasNotServicedTimePoints(timePoints); vehId++) {
            TimePoint startPoint = getNextNotServicedTimePoint(timePoints);
            trips.addAll(generateVehicleTrips(vehId, startPoint, timePoints));
        }

        return trips;
    }

    private List<TimePoint> generateTimePoints() {
        val timePoints = new ArrayList<TimePoint>();
        LocalTime nextTimePoint = startTimeInbound;
        while (nextTimePoint.isBefore(endTimeInbound)) {
            timePoints.add(new TimePoint(INBOUND, nextTimePoint, false));
            nextTimePoint = nextTimePoint.plus(headway);
        }
        nextTimePoint = startTimeOutbound;
        while (nextTimePoint.isBefore(endTimeOutbound)) {
            timePoints.add(new TimePoint(OUTBOUND, nextTimePoint, false));
            nextTimePoint = nextTimePoint.plus(headway);
        }
        return timePoints;
    }

    private List<Trip> generateVehicleTrips(int vehicleId, TimePoint startPoint, List<TimePoint> timePoints) {
        val trips = new ArrayList<Trip>();

        while (true) {
            val trip = generateTrip(vehicleId, startPoint);
            trips.add(trip);

            //TODO maybe remove last trip and break if its arrival time is after end time? May be some parameter to indicate possible lateness?
            val currentTime = getArrivalTime(trip);
            val currentDirection = INBOUND.equals(startPoint.getDirection()) ? OUTBOUND : INBOUND;
            val nextPointOpt = findNextNotServicedTimePointAfter(timePoints, currentTime, currentDirection);

            if (nextPointOpt.isPresent()) {
                startPoint = nextPointOpt.get();
            } else {
                break;
            }
        }

        return trips;
    }

    private Trip generateTrip(int vehicleId, TimePoint timePoint) {
        timePoint.setServiced(true);
        val route = INBOUND.equals(timePoint.getDirection()) ? line.getInboundRoute() : line.getOutboundRoute();
        return new Trip(
            vehicleId,
            calculateStops(timePoint.getTime(), route, averageSpeed));
    }

    private List<Stop> calculateStops(LocalTime startTime, Route route, Quantity<Speed> averageSpeed) {
        val stops = new ArrayList<Stop>();

        val averageSpeedValue = averageSpeed.to(Units.METRE_PER_SECOND).getValue().longValue();

        val headStation = route.getHead();
        val headStationDwellTimeOpt = dwellTimeResolver.resolve(startTime, headStation);
        if (!headStationDwellTimeOpt.isPresent() && failFast)
            throw new ScheduleGeneratorException("Failed to resolve dwell time for station " + headStation);

        val headStationDwellTime = headStationDwellTimeOpt.orElse(Duration.ZERO);
        stops.add(new Stop(startTime, startTime.plus(headStationDwellTime), headStation));
        for (val segment : route) {
            val lastDeparture = stops.get(stops.size() - 1).getDeparture();
            val distTraveled = segment.getLength().to(Units.METRE).getValue().longValue();
            val arrivalTime = lastDeparture.plusSeconds(distTraveled / averageSpeedValue);
            val endStation = segment.getEnd();

            val dwellTimeOpt = dwellTimeResolver.resolve(arrivalTime, endStation);
            if (!dwellTimeOpt.isPresent() && failFast)
                throw new ScheduleGeneratorException("Failed to resolve dwell time for station " + endStation);

            val dwellTime = dwellTimeOpt.orElse(Duration.ZERO);
            stops.add(new Stop(arrivalTime, arrivalTime.plus(dwellTime), endStation));
        }

        return stops;
    }

    private boolean isInTimeToTravel(LocalTime from, TimePoint toPoint) {
        return !Duration.between(from, toPoint.getTime()).minus(minLayover).isNegative();
    }

    private static boolean hasNotServicedTimePoints(List<TimePoint> timePoints) {
        return timePoints.stream().anyMatch(timePoint -> !timePoint.isServiced());
    }

    private static TimePoint getNextNotServicedTimePoint(List<TimePoint> timePoints) {
        return timePoints.stream()
            .filter(timePoint -> !timePoint.isServiced())
            .findFirst()
            .get();
    }

    private Optional<TimePoint> findNextNotServicedTimePointAfter(List<TimePoint> timePoints, LocalTime time, Direction direction) {
        return timePoints.stream()
            .filter(point -> !point.isServiced())
            .filter(point -> point.getDirection().equals(direction))
            .filter(point -> point.getTime().isAfter(time))
            .filter(point -> isInTimeToTravel(time, point))
            .findFirst();
    }

    private static LocalTime getArrivalTime(Trip trip) {
        return trip.getStops().stream()
            .max(Comparator.comparing(Stop::getArrival))
            .map(Stop::getArrival)
            .get();
    }

    enum Direction {
        INBOUND, OUTBOUND
    }

    @AllArgsConstructor
    @Getter
    private class TimePoint {
        private Direction direction;
        private LocalTime time;

        @Setter
        private boolean isServiced;
    }
}
