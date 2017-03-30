package eu.socialedge.hermes.backend.schedule.domain;

import eu.socialedge.hermes.backend.schedule.domain.api.ScheduleGenerator;
import eu.socialedge.hermes.backend.transit.domain.*;
import lombok.*;
import lombok.experimental.Accessors;
import tec.uom.se.quantity.Quantities;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static eu.socialedge.hermes.backend.transit.domain.Direction.INBOUND;
import static eu.socialedge.hermes.backend.transit.domain.Direction.OUTBOUND;

@Builder
@Setter @Accessors(fluent = true)
public class BasicScheduleGenerator implements ScheduleGenerator {

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

    private @NonNull String description;
    private @NonNull Availability availability;

    private LocalTime peakStartTime;
    private LocalTime peakEndTime;
    private int peakFleetIncrease;
    private Duration peakDwellTime;
    private Quantity<Speed> peakAverageSpeed;

//    Break?

    @Override
    public Schedule generate() {
        val trips = magic();

        return new Schedule(description, availability, trips);
    }

    private Set<Trip> magic() {
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

        Set<Trip>  trips = new HashSet<>();
        int vehId = 1;
        while (hasAvailableTimePoints(timePoints)) {
            TimePoint startPoint = getNextAvailableTimePoint(timePoints);
            List<Trip> vehicleTrips = generateVehicleTrips(vehId, startPoint, timePoints);
            trips.addAll(vehicleTrips);
            vehId++;
        }

        return trips;
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
            Optional<TimePoint> nextPoint = findNextAvailableAfterTime(timePoints, currentTime, direction.get());

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

    private Trip genTrip(int vehId, TimePoint timePoint) {
        timePoint.isServiced(true);
        return null; //TODO
    }

    private List<Stop> calculateStopTimes(LocalTime startTime, Shape shape, Quantity<Speed> averageSpeed, Duration dwellTime) {
        val stopTimes = new ArrayList<Stop>();

        for (Station station: route.stations()) {
            //Calculate distance from start point to current stop
            val distTravelled = shape.shapePoints().stream()
                .filter(shapePoint -> station.location().equals(shapePoint.location()))
                .findFirst().orElseThrow(RuntimeException::new) // TODO Do something about it
                .distanceTraveled();

            val distValue = distTravelled.to(Units.METRE).getValue().longValue(); // How much meters from start point
            val timeValue = averageSpeed.to(Units.METRE_PER_SECOND).getValue().longValue(); //speed in meters per second
            val arrivalTime = startTime.plusSeconds(distValue / timeValue); // arrival time to current stop

            stopTimes.add(new Stop(arrivalTime, arrivalTime.plus(dwellTime), station));
        }

        return stopTimes;
    }


//TODO remove it
    public static void main(String[] args) {
        val station = new Station("name", new HashSet<VehicleType>() {{ add(VehicleType.BUS);}}, new Location(2.3, 3.2));
        val route = new Route("code",
            VehicleType.BUS,
            Collections.singletonList(station),
            new Shape(Collections.singletonList(new ShapePoint(new Location(2.3, 3.2), Quantities.getQuantity(140, Units.METRE)))));
        val generator = BasicScheduleGenerator.builder()
            .averageSpeed(Quantities.getQuantity(10, Units.METRE_PER_SECOND))
            .dwellTime(Duration.ofMinutes(1))
            .endTimeInbound(LocalTime.MIDNIGHT)
            .endTimeOutbound(LocalTime.MIDNIGHT)
            .startTimeInbound(LocalTime.NOON)
            .startTimeOutbound(LocalTime.NOON)
            .headway(Duration.ofHours(1))
            .fleetSize(10)
            .minLayover(Duration.ofMinutes(4))
            .route(route).build();

        //canTravel
        isTrue(generator.canTravel(LocalTime.of(10, 10), new TimePoint(INBOUND, LocalTime.of(10, 15), true)));
        isFalse(generator.canTravel(LocalTime.of(10, 15), new TimePoint(INBOUND, LocalTime.of(10, 15), true)));
        isFalse(generator.canTravel(LocalTime.of(10, 25), new TimePoint(INBOUND, LocalTime.of(10, 15), true)));
        isTrue(generator.canTravel(LocalTime.of(10, 11), new TimePoint(INBOUND, LocalTime.of(10, 15), true)));
        isFalse(generator.canTravel(LocalTime.of(10, 13), new TimePoint(INBOUND, LocalTime.of(10, 15), true)));

        //hasAvailableTimePoints
        isFalse(hasAvailableTimePoints(Arrays.asList(new TimePoint(INBOUND, LocalTime.of(10, 15), true), new TimePoint(INBOUND, LocalTime.of(10, 15), true), new TimePoint(INBOUND, LocalTime.of(10, 15), true))));
        isTrue(hasAvailableTimePoints(Arrays.asList(new TimePoint(INBOUND, LocalTime.of(10, 15), false), new TimePoint(INBOUND, LocalTime.of(10, 15), false), new TimePoint(INBOUND, LocalTime.of(10, 15), false))));
        isTrue(hasAvailableTimePoints(Arrays.asList(new TimePoint(INBOUND, LocalTime.of(10, 15), true), new TimePoint(INBOUND, LocalTime.of(10, 15), false), new TimePoint(INBOUND, LocalTime.of(10, 15), false))));
        isTrue(hasAvailableTimePoints(Arrays.asList(new TimePoint(INBOUND, LocalTime.of(10, 15), false), new TimePoint(INBOUND, LocalTime.of(10, 15), true), new TimePoint(INBOUND, LocalTime.of(10, 15), true))));

        //getNextAvailableTimePoint
        List<TimePoint> points = Arrays.asList(new TimePoint(INBOUND, LocalTime.of(11, 15), false), new TimePoint(INBOUND, LocalTime.of(12, 15), false), new TimePoint(INBOUND, LocalTime.of(13, 15), false));
        isTrue(LocalTime.of(11, 15).equals(getNextAvailableTimePoint(points).time()));
        points = Arrays.asList(new TimePoint(INBOUND, LocalTime.of(11, 15), true), new TimePoint(INBOUND, LocalTime.of(12, 15), false), new TimePoint(INBOUND, LocalTime.of(13, 15), false));
        isTrue(LocalTime.of(12, 15).equals(getNextAvailableTimePoint(points).time()));
        points = Arrays.asList(new TimePoint(INBOUND, LocalTime.of(11, 15), true), new TimePoint(INBOUND, LocalTime.of(12, 15), true), new TimePoint(INBOUND, LocalTime.of(13, 15), false));
        isTrue(LocalTime.of(13, 15).equals(getNextAvailableTimePoint(points).time()));
        points = Arrays.asList(new TimePoint(INBOUND, LocalTime.of(11, 15), true), new TimePoint(INBOUND, LocalTime.of(12, 15), true), new TimePoint(INBOUND, LocalTime.of(13, 15), true));
        boolean thrown = false;
        try {
            getNextAvailableTimePoint(points);
        } catch (RuntimeException e) {
            thrown = true;
        }
        isTrue(thrown);

        //getArrivalTime
        List<Stop> stops = Arrays.asList(new Stop(LocalTime.of(10, 10), LocalTime.of(10, 11), station), new Stop(LocalTime.of(11, 10), LocalTime.of(11, 11), station), new Stop(LocalTime.of(12, 10), LocalTime.of(12, 11), station));
        isTrue(LocalTime.of(12, 10).equals(getArrivalTime(new Trip(INBOUND, route, stops))));
        stops = Arrays.asList(new Stop(LocalTime.of(10, 10), LocalTime.of(10, 11), station));
        isTrue(LocalTime.of(10, 10).equals(getArrivalTime(new Trip(INBOUND, route, stops))));
        stops = Arrays.asList(new Stop(LocalTime.of(11, 10), LocalTime.of(11, 11), station), new Stop(LocalTime.of(10, 10), LocalTime.of(10, 11), station));
        isTrue(LocalTime.of(11, 10).equals(getArrivalTime(new Trip(INBOUND, route, stops))));

        //findNearbyForService
        points = Arrays.asList(new TimePoint(INBOUND, LocalTime.of(11, 15), false), new TimePoint(INBOUND, LocalTime.of(12, 15), false), new TimePoint(INBOUND, LocalTime.of(13, 15), false));
        isTrue(LocalTime.of(12, 15).equals(generator.findNextAvailableAfterTime(points, LocalTime.of(11, 14), INBOUND).get().time()));

        points = Arrays.asList(new TimePoint(INBOUND, LocalTime.of(11, 15), false), new TimePoint(OUTBOUND, LocalTime.of(12, 15), false), new TimePoint(INBOUND, LocalTime.of(13, 15), false));
        isTrue(LocalTime.of(13, 15).equals(generator.findNextAvailableAfterTime(points, LocalTime.of(11, 14), INBOUND).get().time()));

        points = Arrays.asList(new TimePoint(INBOUND, LocalTime.of(11, 15), false), new TimePoint(INBOUND, LocalTime.of(12, 15), false), new TimePoint(INBOUND, LocalTime.of(13, 15), false));
        isTrue(LocalTime.of(13, 15).equals(generator.findNextAvailableAfterTime(points, LocalTime.of(12, 16), INBOUND).get().time()));

        points = Arrays.asList(new TimePoint(INBOUND, LocalTime.of(11, 15), false), new TimePoint(INBOUND, LocalTime.of(12, 15), false), new TimePoint(INBOUND, LocalTime.of(13, 15), false));
        isTrue(LocalTime.of(12, 15).equals(generator.findNextAvailableAfterTime(points, LocalTime.of(11, 15), INBOUND).get().time()));

        points = Arrays.asList(new TimePoint(INBOUND, LocalTime.of(11, 15), false), new TimePoint(INBOUND, LocalTime.of(12, 15), false), new TimePoint(INBOUND, LocalTime.of(13, 15), false));
        isTrue(LocalTime.of(11, 15).equals(generator.findNextAvailableAfterTime(points, LocalTime.of(11, 10), INBOUND).get().time()));

        points = Arrays.asList(new TimePoint(OUTBOUND, LocalTime.of(11, 15), false), new TimePoint(OUTBOUND, LocalTime.of(12, 15), false), new TimePoint(INBOUND, LocalTime.of(13, 15), false));
        isTrue(LocalTime.of(13, 15).equals(generator.findNextAvailableAfterTime(points, LocalTime.of(11, 10), INBOUND).get().time()));

        points = Arrays.asList(new TimePoint(OUTBOUND, LocalTime.of(11, 15), false), new TimePoint(OUTBOUND, LocalTime.of(12, 15), false), new TimePoint(OUTBOUND, LocalTime.of(13, 15), false));
        isTrue(!generator.findNextAvailableAfterTime(points, LocalTime.of(11, 14), INBOUND).isPresent());

        points = Arrays.asList(new TimePoint(OUTBOUND, LocalTime.of(11, 15), false), new TimePoint(OUTBOUND, LocalTime.of(12, 15), false), new TimePoint(OUTBOUND, LocalTime.of(13, 15), false));
        isTrue(!generator.findNextAvailableAfterTime(points, LocalTime.of(13, 16), OUTBOUND).isPresent());

    }

    private static void isTrue(boolean value) {
        if (!value) {
            throw new RuntimeException("Not true");
        }
    }

    private static void isFalse(boolean value) {
        if (value) {
            throw new RuntimeException("Not true");
        }
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

