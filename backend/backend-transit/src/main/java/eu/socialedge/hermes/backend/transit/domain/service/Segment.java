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

package eu.socialedge.hermes.backend.transit.domain.service;

import eu.socialedge.hermes.backend.transit.domain.infra.Station;
import eu.socialedge.hermes.backend.transit.domain.geo.Location;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * {@code Segment} represents an edge of the journey graph,
 * defined by {@link Route}:
 * <pre>
 *           Route ST1 -> ST3
 * ____________________________________
 * |  ST1      wp1            ST3     |
 * |   $ ------ *              $      |
 * |             \      ST2    |      |
 * |          wp2 * ---- $ --- * wp3  |
 * ------------------------------------
 *
 *  == Route {[
 *      segment{start: ST1, end: ST2, waypoints: [wp1, wp2]}
 *      segment{start: ST2, end: ST3, waypoints: [wp3]}
 *  ]}</pre>
 *
 * @see <a href="https://goo.gl/FMmU83">
 *     Google Static Transit (GTFS) - shapes.txt File</a>
 */
@Document
@ToString @EqualsAndHashCode
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Segment {

    @DBRef @Getter
    private final @NotNull Station begin;

    @DBRef @Getter
    private final @NotNull Station end;

    @Getter
    private final Quantity<Length> length;

    private final @NotNull List<Location> waypoints = new ArrayList<>();

    public Segment(Station begin, Station end, Quantity<Length> length, List<Location> waypoints) {
        this.begin = notNull(begin);
        this.end = notNull(end);

        if (length != null && length.getValue().longValue() < 0)
            throw new IllegalArgumentException("Length cant be < 0");

        this.length = length;

        if (waypoints != null)
            this.waypoints.addAll(waypoints);
    }

    public static Segment of(Station begin, Station end, Quantity<Length> length, List<Location> waypoints) {
        return new Segment(begin, end, length, waypoints);
    }

    public static Segment of(Station begin, Station end, Quantity<Length> length, Location... waypoints) {
        return new Segment(begin, end, length, asList(waypoints));
    }

    public Segment(Station begin, Station end, List<Location> waypoints) {
        this(begin, end, null, waypoints);
    }

    public static Segment of(Station begin, Station end, List<Location> waypoints) {
        return new Segment(begin, end, waypoints);
    }

    public static Segment of(Station begin, Station end, Location... waypoints) {
        return new Segment(begin, end, asList(waypoints));
    }

    public Segment(Station begin, Station end, Quantity<Length> length) {
        this(begin, end, length, null);
    }

    public static Segment of(Station begin, Station end, Quantity<Length> length) {
        return new Segment(begin, end, length);
    }

    public Segment(Station begin, Station end) {
        this(begin, end, null, null);
    }

    public static Segment of(Station begin, Station end) {
        return new Segment(begin, end);
    }

    public List<Location> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }
}
