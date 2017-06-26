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

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.socialedge.hermes.backend.transit.domain.Station;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notEmpty;

/**
 * Transit Routes define {@link Station} waypoints for a journey
 * taken by a vehicle along a transit line.
 * <p>
 * Route consists of {@link Segment}s that represent edges of
 * the journey graph:
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
 * @see <a href="https://goo.gl/RXKK9c">
 *     Google Static Transit (GTFS) - trips.txt File</a>
 */
@Document
@ToString @EqualsAndHashCode
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Route implements Iterable<Segment> {

    private final @NotEmpty List<Segment> segments = new ArrayList<>();

    public Route(List<Segment> segments) {
        if (!areInterconnectedSegments(segments))
            throw new IllegalArgumentException("Segments must be interconnected (seg[i-1].end === seg[i].start)");

        this.segments.addAll(notEmpty(segments));
    }

    public static Route of(Segment... segments) {
        return new Route(asList(segments));
    }

    public static Route of(List<Segment> segments) {
        return new Route(segments);
    }

    /**
     * Retrieves the start {@code Station} from the head (first) segment
     *
     * @return first {@code Station} in the {@code Route}
     */
    public Station getHead() {
        return segments.get(0).getBegin();
    }

    /**
     * Retrieves the end {@code Station} from the tail (last) segment
     *
     * @return last {@code Station} in the {@code Route}
     */
    public Station getTail() {
        return segments.get(segments.size() - 1).getEnd();
    }

    @Override
    public Iterator<Segment> iterator() {
        val interIter = segments.iterator();
        return new Iterator<Segment>() {
            @Override
            public boolean hasNext() {
                return interIter.hasNext();
            }

            @Override
            public Segment next() {
                return interIter.next();
            }
        };
    }

    public Stream<Segment> stream() {
        return segments.stream();
    }

    private boolean areInterconnectedSegments(List<Segment> segments) {
        if (segments.size() == 1)
            return true;

        for (int i = 1; i < segments.size(); i++) {
            val prevSegment = segments.get(i - 1);
            val currSegment = segments.get(i);

            if (!prevSegment.getEnd().equals(currSegment.getBegin()))
                return false;
        }

        return true;
    }

    @JsonIgnore
    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(iterator().next().getBegin());
        stations.addAll(stream().map(Segment::getEnd).collect(toList()));
        return stations;
    }

}
