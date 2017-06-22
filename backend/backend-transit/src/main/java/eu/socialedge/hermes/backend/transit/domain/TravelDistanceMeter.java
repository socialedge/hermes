/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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

package eu.socialedge.hermes.backend.transit.domain;

import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface TravelDistanceMeter {

    default Quantity<Length> calculate(Location origin, Location destination) {
        val distances = calculate(Collections.singletonList(origin), Collections.singletonList(destination));
        return distances.values().iterator().next();
    }

    Map<Pair<Location, Location>, Quantity<Length>> calculate(List<Location> origins, List<Location> destinations);

}
