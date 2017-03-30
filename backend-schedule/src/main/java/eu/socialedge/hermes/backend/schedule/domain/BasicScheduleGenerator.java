package eu.socialedge.hermes.backend.schedule.domain;

import eu.socialedge.hermes.backend.schedule.domain.api.ScheduleGenerator;
import eu.socialedge.hermes.backend.transit.domain.*;
import lombok.*;
import lombok.experimental.Accessors;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static eu.socialedge.hermes.backend.schedule.domain.Direction.INBOUND;
import static eu.socialedge.hermes.backend.schedule.domain.Direction.OUTBOUND;

@Builder
@Setter @Accessors(fluent = true)
public class BasicScheduleGenerator implements ScheduleGenerator {

    private @NonNull Route route;

    private @NonNull String description;
    private @NonNull Availability availability;

    private @NonNull LocalTime startTimeInbound;
    private @NonNull LocalTime startTimeOutbound;
    private @NonNull LocalTime endTimeInbound;
    private @NonNull LocalTime endTimeOutbound;
    private @NonNull Duration headway;
    private @NonNull Integer fleetSize;
    private @NonNull Duration dwellTime;
    private @NonNull Quantity<Speed> averageSpeed;
    private @NonNull Duration minLayover;

//    Break?

    @Override
    public Schedule generate() {
        val trips = magic();

        return new Schedule(description, availability, trips);
    }

    private Set<Trip> magic() {
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

        val trips = new HashSet<Trip>();
        int vehId = 1;
        while (hasAvailableTimePoints(timePoints)) {
            val startPoint = getNextAvailableTimePoint(timePoints);
            val vehicleTrips = generateVehicleTrips(vehId, startPoint, timePoints);
            trips.addAll(vehicleTrips);
            vehId++;
        }

        return trips;
    }

    private List<Trip> generateVehicleTrips(int vehicleId, TimePoint startPoint, List<TimePoint> timePoints) {
        val trips = new ArrayList<Trip>();
        val directionToggle = new DirectionToggle(startPoint.direction());

        boolean canTravel = startPoint.time().isBefore(directionToggle.get().equals(INBOUND) ? endTimeOutbound : endTimeInbound);
        TimePoint currentPoint = startPoint;
        while (canTravel) {
            val trip = genTrip(vehicleId, currentPoint);
            directionToggle.turn();

            val currentTime = getArrivalTime(trip);
            val nextPoint = findNextAvailableAfterTime(timePoints, currentTime, directionToggle.get());

            if (nextPoint.isPresent()) {
                currentPoint = nextPoint.get();
            } else {
                canTravel = false;
            }
        }

        return trips;
    }

    private boolean canTravel(LocalTime from, TimePoint toPoint) {
        return !Duration.between(from, toPoint.time()).minus(minLayover).isNegative();
    }

    private static boolean hasAvailableTimePoints(List<TimePoint> timePoints) {
        return timePoints.stream().anyMatch(timePoint -> !timePoint.isServiced());
    }

    private static TimePoint getNextAvailableTimePoint(List<TimePoint> timePoints) {
        return timePoints.stream()
            .filter(timePoint -> !timePoint.isServiced())
            .sorted(Comparator.comparing(TimePoint::isServiced))
            .findFirst()
            .get();
    }

    private static LocalTime getArrivalTime(Trip trip) {
        return trip.stopTimes().stream()
            .max(Comparator.comparing(Stop::arrival))
            .map(Stop::arrival)
            .get();
    }

    private Optional<TimePoint> findNextAvailableAfterTime(List<TimePoint> timePoints, LocalTime time, Direction direction) {
        return timePoints.stream()
            .sorted(Comparator.comparing(TimePoint::time))
            .filter(point -> !point.isServiced())
            .filter(point -> point.direction().equals(direction))
            .filter(point -> point.time().isAfter(time))
            .filter(point -> canTravel(time, point))
            .findFirst();
    }

    private Trip genTrip(int vehicleId, TimePoint timePoint) {
        timePoint.isServiced(true);
        return new Trip(eu.socialedge.hermes.backend.transit.domain.Direction.INBOUND, route, "headsign", calculateStopTimes(timePoint.time(), route.shape(), averageSpeed, dwellTime));
    }

    private List<Stop> calculateStopTimes(LocalTime startTime, Shape shape, Quantity<Speed> averageSpeed, Duration dwellTime) {
        val stopTimes = new ArrayList<Stop>();

        for (Station station: route.stations()) {
            val distTravelled = shape.shapePoints().stream()
                .filter(shapePoint -> station.location().equals(shapePoint.location()))
                .findFirst()
                .orElseThrow(() -> new ScheduleGeneratorException("Could not match stations with route shape. Station not found at location + " + station.location()))
                .distanceTraveled();

            val distValue = distTravelled.to(Units.METRE).getValue().longValue();
            val timeValue = averageSpeed.to(Units.METRE_PER_SECOND).getValue().longValue();
            val arrivalTime = startTime.plusSeconds(distValue / timeValue);

            stopTimes.add(new Stop(arrivalTime, arrivalTime.plus(dwellTime), station));
        }

        return stopTimes;
    }
}

enum Direction {
    INBOUND, OUTBOUND
}

@AllArgsConstructor
@Getter @Setter @Accessors(fluent = true)
class TimePoint {
    private Direction direction;
    private LocalTime time;
    private boolean isServiced;
}

@AllArgsConstructor
class DirectionToggle {
    Direction currentDirection;

    void turn() {
        currentDirection = currentDirection.equals(INBOUND) ? OUTBOUND : INBOUND;
    }

    Direction get() {
        return currentDirection;
    }
}

