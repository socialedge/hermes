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
package eu.socialedge.hermes.application.domain.timetable;

import eu.socialedge.hermes.application.domain.timetable.dto.ScheduleSpecification;
import eu.socialedge.hermes.application.domain.timetable.dto.ScheduleSpecificationMapper;
import eu.socialedge.hermes.application.domain.timetable.dto.TripSpecification;
import eu.socialedge.hermes.application.domain.timetable.dto.TripSpecificationMapper;
import eu.socialedge.hermes.application.ext.PATCH;
import eu.socialedge.hermes.application.ext.Resource;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.timetable.TripId;
import eu.socialedge.hermes.domain.transit.RouteId;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static eu.socialedge.hermes.util.Values.isNotNull;

@Resource
@Path("/v1.2/schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TimetableResource {

    private final TimetableService timetableService;
    private final ScheduleSpecificationMapper scheduleSpecMapper;
    private final TripSpecificationMapper tripSpecMapper;

    @Inject
    public TimetableResource(TimetableService timetableService,
                             ScheduleSpecificationMapper scheduleSpecMapper,
                             TripSpecificationMapper tripSpecMapper) {
        this.timetableService = timetableService;
        this.scheduleSpecMapper = scheduleSpecMapper;
        this.tripSpecMapper = tripSpecMapper;
    }

    @POST
    public Response createSchedule(@NotNull @Valid ScheduleSpecification data,
                                   @Context UriInfo uriInfo) {
        timetableService.createSchedule(data);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(data.id)
                .build()).build();
    }

    @POST
    @Path("/{scheduleId}/trips")
    public Response createTrip(@PathParam("scheduleId") ScheduleId scheduleId,
                               @NotNull @Valid TripSpecification data,
                               @Context UriInfo uriInfo) {
        timetableService.createTrip(scheduleId, data);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(data.id)
                .build()).build();
    }

    @GET
    @Path("/{scheduleId}")
    public ScheduleSpecification readSchedule(@PathParam("scheduleId") ScheduleId scheduleId) {
        return scheduleSpecMapper.toDto(timetableService.fetchSchedule(scheduleId));
    }

    @GET
    @Path("/{scheduleId}/trips/{tripId}")
    public TripSpecification readTrip(@PathParam("scheduleId") ScheduleId scheduleId,
                                      @PathParam("tripId") TripId tripId) {
        return tripSpecMapper.toDto(timetableService.fetchTrip(scheduleId, tripId));
    }

    @GET
    public Collection<ScheduleSpecification> readAllSchedules(@QueryParam("routeId") RouteId routeId) {
        Collection<Schedule> schedules
                = isNotNull(routeId) ? timetableService.fetchAllSchedulesByRouteId(routeId)
                                     : timetableService.fetchAllSchedules();

        return schedules.stream().map(scheduleSpecMapper::toDto).collect(Collectors.toList());
    }

    @GET
    @Path("/{scheduleId}/trips")
    public Collection<TripSpecification> readAllTrips(@PathParam("scheduleId") ScheduleId scheduleId) {
        return timetableService.fetchAllTrips(scheduleId).stream()
                .map(tripSpecMapper::toDto).collect(Collectors.toList());
    }

    @PATCH
    @Path("/{scheduleId}")
    public Response updateSchedule(@PathParam("scheduleId") ScheduleId scheduleId,
                                   @NotNull ScheduleSpecification data) {
        timetableService.updateSchedule(scheduleId, data);

        return Response.ok().build();
    }

    @PATCH
    @Path("/{scheduleId}/trips/{tripId}")
    public Response updateTrip(@PathParam("scheduleId") ScheduleId scheduleId,
                               @PathParam("tripId") TripId tripId,
                               @NotNull TripSpecification data) {
        timetableService.updateTrip(scheduleId, tripId, data);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{scheduleId}")
    public Response deleteSchedule(@PathParam("scheduleId") ScheduleId scheduleId) {
        timetableService.deleteSchedule(scheduleId);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{scheduleId}/trips/{tripId}")
    public Response deleteTrip(@PathParam("scheduleId") ScheduleId scheduleId,
                               @PathParam("tripId") TripId tripId) {
        timetableService.deleteTrip(scheduleId, tripId);
        return Response.noContent().build();
    }
}
