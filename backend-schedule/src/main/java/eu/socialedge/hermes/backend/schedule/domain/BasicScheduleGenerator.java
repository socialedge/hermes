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

    private @NonNull Route routeInbound;
    private @NonNull LocalTime startTimeInbound;
    private @NonNull LocalTime endTimeInbound;

    private @NonNull Route routeOutbound;
    private @NonNull LocalTime startTimeOutbound;
    private @NonNull LocalTime endTimeOutbound;

    private @NonNull Duration headway;
    private @NonNull Duration dwellTime;
    private @NonNull Quantity<Speed> averageSpeed;
    private @NonNull Duration minLayover;

//    Break?

    @Override
    public Schedule generate() {
        List<Trip> trips = magic();

        return new Schedule(description, availability, trips);
    }

    private List<Trip> magic() {
        List<TimePoint> timePoints = new ArrayList<>();
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

        List<Trip> trips = new ArrayList<>();
        int vehId = 1;
        while (hasNotServicedTimePoints(timePoints)) {
            TimePoint startPoint = getNextNotServicedTimePoint(timePoints);
            trips.addAll(generateVehicleTrips(vehId, startPoint, timePoints));
            vehId++;
        }

        return trips;
    }

    private List<Trip> generateVehicleTrips(int vehicleId, TimePoint startPoint, List<TimePoint> timePoints) {
        List<Trip> trips = new ArrayList<>();
        Direction currentDirection = startPoint.direction();

        // TODO this and further checks must make sure that vehicle won't be operating after endTime
        boolean canTravel = startPoint.time().isBefore(INBOUND.equals(currentDirection) ? endTimeOutbound : endTimeInbound);
        TimePoint currentPoint = startPoint;
        while (canTravel) {
            Trip trip = generateTrip(vehicleId, currentPoint);
            trips.add(trip);
            currentDirection = INBOUND.equals(currentDirection) ? OUTBOUND : INBOUND;

            LocalTime currentTime = getArrivalTime(trip);
            Optional<TimePoint> nextPointOpt = findNextNotServicedTimePointAfter(timePoints, currentTime, currentDirection);

            if (nextPointOpt.isPresent()) {
                currentPoint = nextPointOpt.get();
            } else {
                canTravel = false;
            }
        }

        return trips;
    }

    private boolean isInTimeToTravel(LocalTime from, TimePoint toPoint) {
        return !Duration.between(from, toPoint.time()).minus(minLayover).isNegative();
    }

    private static boolean hasNotServicedTimePoints(List<TimePoint> timePoints) {
        return timePoints.stream().anyMatch(timePoint -> !timePoint.isServiced());
    }

    private static TimePoint getNextNotServicedTimePoint(List<TimePoint> timePoints) {
        return timePoints.stream()
            .filter(timePoint -> !timePoint.isServiced())
            .sorted(Comparator.comparing(TimePoint::time))
            .findFirst()
            .get();
    }

    private static LocalTime getArrivalTime(Trip trip) {
        return trip.stopTimes().stream()
            .max(Comparator.comparing(Stop::arrival))
            .map(Stop::arrival)
            .get();
    }

    private Optional<TimePoint> findNextNotServicedTimePointAfter(List<TimePoint> timePoints, LocalTime time, Direction direction) {
        return timePoints.stream()
            .sorted(Comparator.comparing(TimePoint::time))
            .filter(point -> !point.isServiced())
            .filter(point -> point.direction().equals(direction))
            .filter(point -> point.time().isAfter(time))
            .filter(point -> isInTimeToTravel(time, point))
            .findFirst();
    }

    private Trip generateTrip(int vehicleId, TimePoint timePoint) {
        timePoint.isServiced(true);
        Route route = INBOUND.equals(timePoint.direction()) ? routeInbound : routeOutbound;
        return new Trip(
            route,
            vehicleId,
            route.stations().get(route.stations().size() - 1).name(),
            calculateStops(timePoint.time(), route, averageSpeed, dwellTime));
    }

    private List<Stop> calculateStops(LocalTime startTime, Route route, Quantity<Speed> averageSpeed, Duration dwellTime) {
        List<Stop> stops = new ArrayList<>();

        long speedValue = averageSpeed.to(Units.METRE_PER_SECOND).getValue().longValue();

        for (int i = 0; i < route.stations().size(); i++) {
            Station station = route.stations().get(i);
            Quantity<Length> distTravelled = route.shape().shapePoints().stream()
                .filter(shapePoint -> station.location().equals(shapePoint.location()))
                .findFirst()
                .orElseThrow(() -> new ScheduleGeneratorException("Could not match stations with route shape. Station not found at location + " + station.location()))
                .distanceTraveled();

            long distValue = distTravelled.to(Units.METRE).getValue().longValue();
            LocalTime arrivalTime = startTime.plusSeconds(distValue / speedValue).plus(dwellTime.multipliedBy(i));

            stops.add(new Stop(arrivalTime, arrivalTime.plusSeconds(dwellTime.getSeconds()), station));
        }

        return stops;
    }

    enum Direction {
        INBOUND, OUTBOUND
    }

    @AllArgsConstructor
    @Getter @Setter @Accessors(fluent = true)
    private class TimePoint {
        private Direction direction;
        private LocalTime time;
        private boolean isServiced;
    }
}
