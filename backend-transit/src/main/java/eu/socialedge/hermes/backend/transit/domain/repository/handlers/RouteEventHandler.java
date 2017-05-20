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
package eu.socialedge.hermes.backend.transit.domain.repository.handlers;

import eu.socialedge.hermes.backend.transit.domain.Route;
import eu.socialedge.hermes.backend.transit.domain.ShapeFactory;
import eu.socialedge.hermes.backend.transit.domain.Station;
import lombok.val;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

import java.util.stream.Collectors;

// TODO: Must be deprecated by #119 (Replace Route's stations with generic waypoints)
@RepositoryEventHandler
public class RouteEventHandler {

    private ShapeFactory shapeFactory;

    public RouteEventHandler(ShapeFactory shapeFactory) {
        this.shapeFactory = shapeFactory;
    }

    @HandleBeforeSave
    @HandleBeforeCreate
    public void creteRouteShape(Route route) {
        val locations = route.getStations().stream()
            .map(Station::getLocation)
            .collect(Collectors.toList());
        val shape = shapeFactory.create(locations);

        route.setShape(shape);
    }
}
