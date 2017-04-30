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

import eu.socialedge.hermes.backend.schedule.domain.api.ScheduleGenerator;
import eu.socialedge.hermes.backend.transit.domain.*;
import lombok.*;
import lombok.experimental.Accessors;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static eu.socialedge.hermes.backend.schedule.domain.BasicScheduleGenerator.Direction.INBOUND;
import static eu.socialedge.hermes.backend.schedule.domain.BasicScheduleGenerator.Direction.OUTBOUND;

@Builder
@Setter @Accessors(fluent = true)
public class BasicScheduleGenerator implements ScheduleGenerator {

    private @NonNull String description;
    private @NonNull Availability availability;

    private @NonNull Line line;

    private @NonNull LocalTime startTimeInbound;
    private @NonNull LocalTime endTimeInbound;

    private @NonNull LocalTime startTimeOutbound;
    private @NonNull LocalTime endTimeOutbound;

    private @NonNull Duration headway;
    private @NonNull Duration dwellTime;
    private @NonNull Quantity<Speed> averageSpeed;
    private @NonNull Duration minLayover;

    @Override
    public Schedule generate() {
        return new Schedule(description, availability, line, generateTrips());
    }

    private List<Trip> generateTrips() {
        val timePoints = generateTimePoints();
        timePoints.sort(Comparator.comparing(TimePoint::time));

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
            val currentDirection = INBOUND.equals(startPoint.direction()) ? OUTBOUND : INBOUND;
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
        timePoint.isServiced(true);
        val route = INBOUND.equals(timePoint.direction()) ? line.inboundRoute() : line.outboundRoute();
        return new Trip(
            route,
            vehicleId,
            route.stations().get(route.stations().size() - 1).name(),
            calculateStops(timePoint.time(), route, averageSpeed, dwellTime));
    }

    private List<Stop> calculateStops(LocalTime startTime, Route route, Quantity<Speed> averageSpeed, Duration dwellTime) {
        val stops = new ArrayList<Stop>();

        val averageSpeedValue = averageSpeed.to(Units.METRE_PER_SECOND).getValue().longValue();

        for (int i = 0; i < route.stations().size(); i++) {
            val station = route.stations().get(i);
            val distTravelled = distanceTraveled(route, station);

            val distValue = distTravelled.to(Units.METRE).getValue().longValue();
            val arrivalTime = startTime.plusSeconds(distValue / averageSpeedValue).plus(dwellTime.multipliedBy(i));

            stops.add(new Stop(arrivalTime, arrivalTime.plusSeconds(dwellTime.getSeconds()), station));
        }

        return stops;
    }

    private boolean isInTimeToTravel(LocalTime from, TimePoint toPoint) {
        return !Duration.between(from, toPoint.time()).minus(minLayover).isNegative();
    }

    private static Quantity<Length> distanceTraveled(Route route, Station station) {
        return route.shape().shapePoints().stream()
            .filter(shapePoint -> station.location().equals(shapePoint.location()))
            .findFirst()
            .map(ShapePoint::distanceTraveled)
            .orElseThrow(() -> new ScheduleGeneratorException("Could not match stations with route shape. Station not found with location + " + station.location()));
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
            .filter(point -> point.direction().equals(direction))
            .filter(point -> point.time().isAfter(time))
            .filter(point -> isInTimeToTravel(time, point))
            .findFirst();
    }

    private static LocalTime getArrivalTime(Trip trip) {
        return trip.stops().stream()
            .max(Comparator.comparing(Stop::arrival))
            .map(Stop::arrival)
            .get();
    }

    enum Direction {
        INBOUND, OUTBOUND
    }

    @AllArgsConstructor
    @Getter @Accessors(fluent = true)
    private class TimePoint {
        private Direction direction;
        private LocalTime time;

        @Setter
        private boolean isServiced;
    }
}
