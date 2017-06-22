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

package eu.socialedge.hermes.backend.transit.domain.repository.handlers;

import eu.socialedge.hermes.backend.transit.domain.DistanceAwareSegmentFactory;
import eu.socialedge.hermes.backend.transit.domain.Line;
import eu.socialedge.hermes.backend.transit.domain.Route;
import eu.socialedge.hermes.backend.transit.domain.Segment;
import lombok.val;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.util.ArrayList;

/**
 * Fills in {@link eu.socialedge.hermes.backend.transit.domain.Segment#length}
 * in case if it's empty
 *
 * @see <a href="https://github.com/socialedge/hermes/issues/148">
 *     Issue 148 - Remove RouteSegmentLengthHook and hook length on the app layer</a>
 */
@Deprecated
@RepositoryEventHandler
public class RouteSegmentLengthHook {

    private final DistanceAwareSegmentFactory segmentFactory;

    public RouteSegmentLengthHook(DistanceAwareSegmentFactory segmentFactory) {
        this.segmentFactory = segmentFactory;
    }

    @HandleBeforeSave
    @HandleBeforeCreate
    public void beforeLineSaveCreateHandler(Line line) {
        val inboundRouteSegments = new ArrayList<Segment>();
        for (Segment s : line.getInboundRoute()) {
            if (s.getLength() == null || s.getLength().getValue().doubleValue() == 0) {
                inboundRouteSegments.add(segmentFactory.factory(s.getBegin(), s.getEnd(), s.getWaypoints()));
            } else {
                inboundRouteSegments.add(s);
            }
        }
        line.setInboundRoute(Route.of(inboundRouteSegments));

        if (line.isBidirectionalLine()) {
            val outboundRouteSegments = new ArrayList<Segment>();
            for (Segment s : line.getOutboundRoute()) {
                if (s.getLength() == null || s.getLength().getValue().doubleValue() == 0) {
                    outboundRouteSegments.add(segmentFactory.factory(s.getBegin(), s.getEnd(), s.getWaypoints()));
                } else {
                    outboundRouteSegments.add(s);
                }
            }
            line.setOutboundRoute(Route.of(outboundRouteSegments));
        }
    }
}
