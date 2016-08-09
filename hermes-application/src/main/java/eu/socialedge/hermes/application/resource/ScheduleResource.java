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
package eu.socialedge.hermes.application.resource;

import eu.socialedge.hermes.application.ext.PATCH;
import eu.socialedge.hermes.application.ext.Resource;
import eu.socialedge.hermes.application.resource.spec.ScheduleSpecification;
import eu.socialedge.hermes.application.service.ScheduleService;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleId;
import eu.socialedge.hermes.domain.transit.RouteId;

import java.util.Collection;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static java.util.Objects.isNull;

@Resource
@Path("/v1.1/schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScheduleResource {

    private final ScheduleService scheduleService;

    @Inject
    public ScheduleResource(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @POST
    public Response create(@NotNull @Valid ScheduleSpecification spec, @Context UriInfo uriInfo) {
        scheduleService.createSchedule(spec);

        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(spec.scheduleId)
                .build()).build();
    }

    @GET
    @Path("/{scheduleId}")
    public Schedule read(@PathParam("scheduleId") @NotNull ScheduleId scheduleId) {
        return scheduleService.fetchSchedule(scheduleId)
                .orElseThrow(() -> new NotFoundException("Failed to find schedule. Id = " + scheduleId));
    }

    @GET
    public Collection<Schedule> read(@QueryParam("routeId") RouteId routeId) {
        return !isNull(routeId) ? scheduleService.fetchAllSchedulesByRouteId(routeId)
                                    : scheduleService.fetchAllSchedules();
    }

    @PATCH
    @Path("/{scheduleId}")
    public Response update(@PathParam("scheduleId") @NotNull ScheduleId scheduleId,
                           @NotNull ScheduleSpecification spec) {
        scheduleService.updateSchedule(scheduleId, spec);

        return Response.ok().build();
    }

    @DELETE
    @Path("/{scheduleId}")
    public Response delete(@PathParam("scheduleId") @NotNull ScheduleId scheduleId) {
        boolean wasDeleted = scheduleService.deleteSchedule(scheduleId);
        if (!wasDeleted)
            throw new NotFoundException("Failed to find schedule to delete. Id = " + scheduleId);

        return Response.noContent().build();
    }
}
