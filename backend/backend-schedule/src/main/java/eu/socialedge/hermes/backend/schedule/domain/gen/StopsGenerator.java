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
import eu.socialedge.hermes.backend.transit.domain.service.Route;
import lombok.val;
import tec.uom.se.unit.Units;

import javax.measure.Quantity;
import javax.measure.quantity.Speed;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class StopsGenerator {

    public static List<Stop> generate(LocalTime startTime, Route route, Quantity<Speed> averageSpeed) {
        val stops = new ArrayList<Stop>();

        val averageSpeedValue = averageSpeed.to(Units.METRE_PER_SECOND).getValue().longValue();
        val headStation = route.getHead();
        stops.add(new Stop(startTime, startTime.plus(headStation.getDwell()), headStation));
        for (val segment : route) {
            val lastDeparture = stops.get(stops.size() - 1).getDeparture();
            val distTraveled = segment.getLength().to(Units.METRE).getValue().longValue();
            val arrivalTime = lastDeparture.plusSeconds(distTraveled / averageSpeedValue);
            val endStation = segment.getEnd();

            stops.add(new Stop(arrivalTime, arrivalTime.plus(headStation.getDwell()), endStation));
        }

        return stops;
    }
}
