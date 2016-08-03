/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
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
package eu.socialedge.hermes.application.v2.resource;

import eu.socialedge.hermes.application.ext.PATCH;
import eu.socialedge.hermes.application.ext.Resource;
import eu.socialedge.hermes.application.v2.resource.spec.TripSpecification;
import eu.socialedge.hermes.application.v2.service.TripService;
import eu.socialedge.hermes.domain.v2.transit.Trip;
import eu.socialedge.hermes.domain.v2.transit.TripId;

import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Resource
@Path("/v2/trips")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TripResource {

    private final TripService tripService;

    @Inject
    public TripResource(TripService tripService) {
        this.tripService = tripService;
    }

    @POST
    public Response create(@NotNull @Valid TripSpecification spec, @Context UriInfo uriInfo) {
        tripService.createTrip(spec);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(spec.tripId)
                .build()).build();
    }

    @GET
    @Path("/{tripId}")
    public Trip read(@PathParam("tripId") @NotNull TripId tripId) {
        Optional<Trip> tripOpt = tripService.fetchTrip(tripId);

        if (!tripOpt.isPresent())
            throw new NotFoundException("Failed to find trip. Id = " + tripId);

        return tripOpt.get();
    }

    @GET
    public Collection<Trip> read() {
        return tripService.fetchAllTrips();
    }

    @PATCH
    @Path("/{tripId}")
    public Response update(@PathParam("tripId") @NotNull TripId tripId,
                           @NotNull TripSpecification spec) {
        tripService.updateTrip(spec);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{tripId}")
    public Response delete(@PathParam("tripId") @NotNull TripId tripId) {
        boolean wasDeleted = tripService.deleteTrip(tripId);
        if (!wasDeleted)
            throw new NotFoundException("Failed to find trip to delete. Id = " + tripId);

        return Response.noContent().build();
    }
}
