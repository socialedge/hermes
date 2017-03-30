package eu.socialedge.hermes.backend.gen.schedule;

import eu.socialedge.hermes.backend.core.*;
import eu.socialedge.hermes.backend.gen.schedule.api.ScheduleGenerator;
import lombok.*;
import lombok.experimental.Accessors;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static eu.socialedge.hermes.backend.core.Direction.INBOUND;
import static eu.socialedge.hermes.backend.core.Direction.OUTBOUND;

@Builder
@Setter @Accessors(fluent = true)
public class BasicScheduleGenerator implements ScheduleGenerator {

    private @NonNull List<Stop> stops;
    private @NonNull Route route;

    private @NonNull LocalTime startTimeInbound;
    private @NonNull LocalTime startTimeOutbound;
    private @NonNull LocalTime endTimeInbound;
    private @NonNull LocalTime endTimeOutbound;
    private @NonNull Duration headway; // Interval between trips
    private @NonNull Integer fleetSize;
    private @NonNull Duration dwellTime; // Time spent at one stop
    private @NonNull Quantity<Speed> averageSpeed;
    private @NonNull Duration minLayover; // Time to wait at the end of the trip before going in opposite direction

    private LocalTime peakStartTime;
    private LocalTime peakEndTime;
    private int peakFleetIncrease;
    private Duration peakDwellTime;
    private Quantity<Speed> peakAverageSpeed;

    //TODO availability

//    Break?

    @Override
    public Schedule generate() {
        val shapeFactory = new GoogleMapsShapeFactory("apiKey"); //TODO api key
        val shape = shapeFactory.create(stops.stream().map(Stop::location).collect(Collectors.toList()));


        // create all trips
        val trips = new HashSet<Trip>();

        val stopTimes = calculateStopTimes(startTimeInbound, shape, averageSpeed, dwellTime);
        //TODO direction?
        //TODO head sign?
        val trip = new Trip(INBOUND, route, "headsign", stopTimes, shape);

        trips.add(trip);
        //stop creating all trips

        return new Schedule("Description", Availability.weekendDays(LocalDate.now(), LocalDate.now().plusDays(1)), trips);
    }

    private void magic() {
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

        List<Trip>  trips = new ArrayList<>();
        int vehId = 1;
        while (hasAvailableTimePoints(timePoints)) {
            TimePoint startPoint = getNextAvailableTimePoint(timePoints);
            List<Trip> vehicleTrips = generateVehicleTrips(vehId, startPoint, timePoints);
            trips.addAll(vehicleTrips);
            vehId++;
        }
    }

    private List<Trip> generateVehicleTrips(int vehicleId, TimePoint startPoint, List<TimePoint> timePoints) {
        List<Trip> trips = new ArrayList<>();
        DirectionToggle direction = new DirectionToggle(startPoint.direction());

        boolean canTravel = startPoint.time().isBefore(direction.get().equals(INBOUND) ? endTimeOutbound : endTimeInbound);
        TimePoint currentPoint = startPoint;
        while (canTravel) {
            Trip trip = genTrip(vehicleId, currentPoint);
            direction.turn();

            LocalTime currentTime = getArrivalTime(trip);
            TimePoint nextPoint = findNearbyForService(timePoints, currentTime, direction.get());

            if (nextPoint == null || !canTravel(currentTime, nextPoint)) { // TODO make optional
                nextPoint = findNextTimeAfter(timePoints, nextPoint);
            }

            if (nextPoint != null) {
                currentPoint = nextPoint;
            } else {
                canTravel = false;
            }
        }

        return trips;
    }

    private boolean canTravel(LocalTime currentTime, TimePoint nextPoint) {
        return Duration.between(currentTime, nextPoint.time()).minus(minLayover).isNegative();
    }

    private boolean hasAvailableTimePoints(List<TimePoint> timePoints) {
        return timePoints.stream().anyMatch(timePoint -> !timePoint.isServiced());
    }

    private TimePoint getNextAvailableTimePoint(List<TimePoint> timePoints) {
        return timePoints.stream()
            .filter(timePoint -> !timePoint.isServiced())
            .sorted(Comparator.comparing(TimePoint::isServiced))
            .findFirst().orElseThrow(RuntimeException::new); //TODO do something about it
    }

    private static LocalTime getArrivalTime(Trip trip) {
        return trip.stopTimes().stream()
            .max(Comparator.comparing(StopTime::arrival))
            .map(StopTime::arrival)
            .orElseThrow(RuntimeException::new);// TODO do something about it
    }

    private static TimePoint findNearbyForService(List<TimePoint> timePoints, LocalTime time, Direction direction) {  // returns next NOT SERVICED nearby
        return timePoints.stream()
            .sorted(Comparator.comparing(TimePoint::time))
            .filter(point -> point.time().isAfter(time))
            .filter(point -> !point.isServiced())
            .filter(point -> point.direction().equals(direction))
            .findFirst().orElse(null);
    }

    private static TimePoint findNextTimeAfter(List<TimePoint> timePoints, TimePoint timePoint) {
        return timePoints.stream()
            .sorted(Comparator.comparing(TimePoint::time))
            .filter(point -> point.time().isAfter(timePoint.time()))
            .filter(point -> !point.isServiced())
            .filter(point -> point.direction().equals(timePoint.direction()))
            .findFirst().orElse(null);
    }

    private Trip genTrip(int vehId, TimePoint timePoint) {
        timePoint.isServiced(true);
        return null; //TODO
    }

    private Set<StopTime> calculateStopTimes(LocalTime startTime, Shape shape, Quantity<Speed> averageSpeed, Duration dwellTime) {
        val stopTimes = new HashSet<StopTime>();

        for (Stop stop: stops) {
            //Calculate distance from start point to current stop
            val distTravelled = shape.shapePoints().stream()
                .filter(shapePoint -> stop.location().equals(shapePoint.location()))
                .findFirst().orElseThrow(RuntimeException::new) // TODO Do something about it
                .distanceTraveled();

            val distValue = distTravelled.to(Units.METRE).getValue().longValue(); // How much meters from start point
            val timeValue = averageSpeed.to(Units.METRE_PER_SECOND).getValue().longValue(); //speed in meters per second
            val arrivalTime = startTime.plusSeconds(distValue / timeValue); // arrival time to current stop

            stopTimes.add(new StopTime(arrivalTime, arrivalTime.plus(dwellTime), stop));
        }

        return stopTimes;
    }
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

