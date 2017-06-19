/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2017 SocialEdge
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

import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notEmpty;

/**
 * Transit Routes define {@link Station} waypoints for a journey
 * taken by a vehicle along a transit line.
 */
@Document
@ToString @EqualsAndHashCode
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
public class Route implements Iterable<Segment> {

    private final @NotEmpty List<Segment> segments = new ArrayList<>();

    public Route(List<Segment> segments) {
        segments.addAll(notEmpty(segments));
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

    @Deprecated
    public List<Station> getStations() {
        return stream().flatMap(sgmt -> Stream.of(sgmt.getBegin(), sgmt.getEnd())).collect(toList());
    }

    @Deprecated
    private Shape shape;

    @Deprecated
    public Shape getShape() {
        return shape;
    }

    @Deprecated
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    @Deprecated
    public Route(String code, VehicleType bus, List<Station> stations, Shape shape) {
        this.shape = shape;

        /*
        s1 i-1
        s2 i    i-1
        s3      i     i-1
        s4             i      i-1
        s5                    i       i-1
        s6                            i
         */

        val segments = new ArrayList<Segment>(stations.size() * 2);
        for (int i = 1; i < stations.size(); i++) {
            segments.add(new Segment(stations.get(i - 1), stations.get(i), null));
        }

        this.segments.addAll(segments);
    }
}
