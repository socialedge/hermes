/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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
package eu.socialedge.hermes.backend.schedule.domain.gen;

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
    public Trip create(LocalTime startTime, String headsign, Route route) {
        val stops = new ArrayList<Stop>();

        val headStop = stopFactory.create(startTime, route.getHead());
        stops.add(headStop);

        var lastDeparture = headStop.getDeparture();
        for (val segment : route) {
            val tailStation = segment.getEnd();
            val length = metres(segment.getLength());

            val travelTime = length / averageSpeedMps;

            val stop = stopFactory.create(lastDeparture.plusSeconds(travelTime), tailStation);
            stops.add(stop);
            lastDeparture = stop.getDeparture();
        }

        return Trip.of(headsign, stops);
    }

    private static long metrePerSecond(Quantity<Speed> speed) {
        return speed.to(Units.METRE_PER_SECOND).getValue().longValue();
    }

    private static long metres(Quantity<Length> lengthQuantity) {
        return lengthQuantity.to(Units.METRE).getValue().longValue();
    }
}
