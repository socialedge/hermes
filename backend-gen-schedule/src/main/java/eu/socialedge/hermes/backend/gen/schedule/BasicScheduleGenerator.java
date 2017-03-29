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

        Map<LocalTime, Trip> trips = new HashMap<>();
        int vehId = 1;

        while (hasAvailableTimePoints(timePoints)) {
            TimePoint startPoint = getNextAvailableTimePoint(timePoints);
            List<Trip> vehicleTrips = generateVehicleTrips(vehId, startPoint, timePoints);





            vehId++;
        }









/*        for (LocalTime startTimeInboundMy : startTimesInbound) {
            if (trips.containsKey(startTimeInboundMy)) {
                break;
            }

            LocalTime nextTripStartTime = startTimeInboundMy;
            while (nextTripStartTime.isBefore(endTimeInbound)) {
                Trip trip = genTrip(vehId, nextTripStartTime);
                trips.put(nextTripStartTime, trip);

                LocalTime terminalEndTime = getArrivalTime(trip);

                LocalTime nearbyStartTimeOutbound = findNearbyForService(startTimesOutbound, terminalEndTime);

                Duration durBeforeNextStartTimeOutbound = Duration.between(nearbyStartTimeOutbound, terminalEndTime);
                if (durBeforeNextStartTimeOutbound.compareTo(minLayover) < 0) {
                    nextTripStartTime = findNextTimeAfter(startTimesOutbound, nearbyStartTimeOutbound);
                } else {
                    nextTripStartTime = nearbyStartTimeOutbound;
                }
            }

            vehId++;
        }*/

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
            TimePoint nextPoint = findNearbyForService(timePoints, currentTime, direction.get()); //TODO null check (null means that vehicle work is done
            currentPoint = canTravel(currentTime, nextPoint) ? nextPoint : findNextTimeAfter(timePoints, nextPoint); //TODO null check (null means that vehicle work is done

            canTravel = false; //TODO implement logic
        }

        return trips;
    }

    private boolean canTravel(LocalTime currentTime, TimePoint nextPoint) {
        return false; //TODO
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
        return null; //TODO
    }

    private static TimePoint findNearbyForService(List<TimePoint> timePoints, LocalTime time, Direction direction) {  // returns next NOT SERVICED nearby
        return null; //TODO
    }

    private static TimePoint findNextTimeAfter(List<TimePoint> timePoints, TimePoint time) {
        return null; //TODO
    }

    private Trip genTrip(int vehId, TimePoint timePoint) {
        //TODO maybe set timePoint.isServiced to be true in this method
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
@Getter @Accessors(fluent = true)
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

    Direction turnAndGet() {
        turn();
        return get();
    }
}

