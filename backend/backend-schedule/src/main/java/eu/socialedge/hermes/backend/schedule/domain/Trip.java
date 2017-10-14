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
package eu.socialedge.hermes.backend.schedule.domain;

import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * A Trip represents a journey taken by a vehicle through {@link Station}.
 *
 * Trips are time-specific - they are defined as a sequence of {@link Stop},
 * so a single Trip represents one journey along a transit route.
 *
 * @see <a href="https://goo.gl/RXKK9c">
 *     Google Static Transit (GTFS) - trips.txt File</a>
 */
@Document
@ToString @EqualsAndHashCode
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Trip  {

    @Getter
    private @NotNull Integer vehicleId;

    @Getter
    private String headsign;

    private @NotEmpty List<Stop> stops;

    public Trip(Integer vehicleId, String headsign, List<Stop> stops) {
        this.vehicleId = notNull(vehicleId);
        this.stops = new ArrayList<>(notEmpty(stops));

        this.headsign = isBlank(headsign) ?
            stops.get(stops.size() - 1).getStation().getName() : headsign;
    }

    public static Trip of(Integer vehicleId, String headsign, List<Stop> stops) {
        return new Trip(vehicleId, headsign, stops);
    }

    public Trip(Integer vehicleId, List<Stop> stops) {
        this(vehicleId, null, stops);
    }

    public static Trip of(Integer vehicleId, List<Stop> stops) {
        return new Trip(vehicleId, stops);
    }

    public boolean addStop(Stop stop) {
        if (stops.contains(stop))
            return false;

        return stops.add(stop);
    }

    public boolean addStop(Stop stop, int index) {
        if (stops.contains(stop))
            return false;

        stops.add(index, stop);
        return true;
    }

    public void removeStop(Stop stop) {
        stops.remove(stop);
    }

    public List<Stop> getStops() {
        return Collections.unmodifiableList(stops);
    }
}
