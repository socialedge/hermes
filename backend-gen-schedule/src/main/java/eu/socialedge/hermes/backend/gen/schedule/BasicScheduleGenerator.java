package eu.socialedge.hermes.backend.gen.schedule;

import eu.socialedge.hermes.backend.core.*;
import eu.socialedge.hermes.backend.gen.schedule.api.ScheduleGenerator;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Setter @Accessors(fluent = true)
public class BasicScheduleGenerator implements ScheduleGenerator {

    private @NonNull List<Stop> stops;
    private @NonNull Route route;

    private @NonNull LocalTime startTime;
    private @NonNull LocalTime endTime;
    private @NonNull Duration headway; // Interval between trips
    private @NonNull Integer fleetSize;
    private @NonNull Duration dwellTime; // Time spent at one stop
    private @NonNull Quantity<Speed> averageSpeed;
    private @NonNull Duration layoverTime; // Time to wait at the end of the trip before going in opposite direction

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

        val stopTimes = calculateStopTimes(startTime, shape, averageSpeed, dwellTime);
        //TODO direction?
        //TODO head sign?
        val trip = new Trip(Direction.INBOUND, route, "headsign", stopTimes, shape);

        trips.add(trip);
        //stop creating all trips

        return new Schedule("Description", Availability.weekendDays(LocalDate.now(), LocalDate.now().plusDays(1)), trips);
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
