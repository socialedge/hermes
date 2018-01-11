package eu.socialedge.hermes.backend.schedule.domain.v2;

import eu.socialedge.hermes.backend.schedule.domain.Stop;
import eu.socialedge.hermes.backend.schedule.domain.Trip;
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import lombok.experimental.var;
import lombok.val;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import java.time.LocalTime;
import java.util.ArrayList;

public class StaticTripFactory implements TripFactory {

    private final StopFactory stopFactory;
    private final long averageSpeedMps;

    public StaticTripFactory(StopFactory stopFactory, Quantity<Speed> averageSpeed) {
        this.stopFactory = stopFactory;
        this.averageSpeedMps = metrePerSecond(averageSpeed);
    }

    @Override
    public Trip create(LocalTime startTime, Integer vehicleId, String headsign, Route route) {
        val stops = new ArrayList<Stop>();

        val headStop = stopFactory.create(startTime, route.getHead());
        stops.add(headStop);

        var lastDeparture = headStop.getDeparture();
        for (val segment : route) {
            val tailStation = segment.getEnd();
            val length = metres(segment.getLength());

            val travelTime = length / averageSpeedMps;

            lastDeparture = lastDeparture.plusSeconds(travelTime);
            stops.add(stopFactory.create(lastDeparture, tailStation));
        }

        return Trip.of(vehicleId, headsign, stops);
    }

    private static long metrePerSecond(Quantity<Speed> speed) {
        return speed.to(Units.METRE_PER_SECOND).getValue().longValue();
    }

    private static long metres(Quantity<Length> lengthQuantity) {
        return lengthQuantity.to(Units.METRE).getValue().longValue();
    }
}
