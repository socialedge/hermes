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
import eu.socialedge.hermes.application.resource.dto.DepartureRefSpec;
import eu.socialedge.hermes.application.resource.dto.DepartureSpec;
import eu.socialedge.hermes.application.resource.dto.ScheduleRefSpec;
import eu.socialedge.hermes.application.service.ScheduleService;
import eu.socialedge.hermes.application.service.StationService;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.timetable.Departure;
import eu.socialedge.hermes.domain.timetable.Schedule;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.stream.Collectors;

import static eu.socialedge.hermes.application.resource.dto.SpecMapper.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Resource
@Path("/v1/schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class ScheduleResource {
    private final ScheduleService scheduleService;
    private final StationService stationService;

    @Inject
    public ScheduleResource(ScheduleService scheduleService, StationService stationService) {
        this.scheduleService = scheduleService;
        this.stationService = stationService;
    }

    @POST
    @Transactional
    public Response create(@NotNull @Valid ScheduleRefSpec scheduleRefSpec, @Context UriInfo uriInfo) {
        String name = scheduleRefSpec.getName();
        String routeCode = scheduleRefSpec.getRouteCode();
        Collection<Departure> departures = unwrapDepartures(scheduleRefSpec.getDepartures());
        LocalDate expDate = scheduleRefSpec.getExpirationDate();

        Schedule persistedSchedule = scheduleService.createSchedule(name, routeCode, departures, expDate);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(persistedSchedule.getId()))
                .build()).build();
    }

    @POST
    @Transactional
    @Path("/{scheduleId}/departures")
    public Response createDeparture(@NotNull @Valid DepartureRefSpec departureRefSpec,
                                    @PathParam("routeCodeId") @Size(min = 1) String routeCodeId,
                                    @PathParam("scheduleId") @Min(1) int scheduleId,
                                    @Context UriInfo uriInfo) {
        String stationCode = departureRefSpec.getStationCodeId();
        LocalTime time = departureRefSpec.getTime();

        scheduleService.createDeparture(scheduleId, routeCodeId, stationCode, time);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(scheduleId))
                .build()).build();
    }

    @GET
    public Collection<?> read(@QueryParam("routeCodeId") String routeCodeId,
                              @QueryParam("detailed") String detailed) {
        Collection<Schedule> schedules =
                isBlank(routeCodeId) ? scheduleService.fetchAllSchedules()
                        : scheduleService.fetchAllSchedulesByRouteCode(routeCodeId);

        if (detailed != null)
            return scheduleSpecs(schedules);

        return scheduleRefSpecs(schedules);
    }

    @GET
    @Path("/{scheduleId}")
    public Object read(@PathParam("scheduleId") @Min(1) int scheduleId,
                       @QueryParam("detailed") String detailed) {
        Schedule schedule = scheduleService.fetchSchedule(scheduleId);

        if (detailed != null)
            return scheduleSpec(schedule);

        return scheduleRefSpec(schedule);
    }

    @GET
    @Path("/{scheduleId}/departures")
    public Collection<DepartureSpec> readDepatures(@PathParam("scheduleId") @Min(1) int scheduleId) {
        return departureSpecs(scheduleService.fetchDepartures(scheduleId));
    }

    @PATCH
    @Transactional
    @Path("/{scheduleId}")
    public Response update(@NotNull ScheduleRefSpec scheduleRefSpec,
                           @PathParam("scheduleId") @Min(1) int scheduleId) {

        String name = scheduleRefSpec.getName();
        Collection<Departure> departures = unwrapDepartures(scheduleRefSpec.getDepartures());
        LocalDate expDate = scheduleRefSpec.getExpirationDate();

        scheduleService.updateSchedule(scheduleId, name, departures, expDate);
        return Response.ok().build();
    }

    @DELETE
    @Transactional
    @Path("/{scheduleId}")
    public Response delete(@PathParam("scheduleId") @Min(1) int scheduleId) {
        scheduleService.removeSchedule(scheduleId);
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{scheduleId}/departures/{stationCodeId}")
    public Response deleteDeparture(@PathParam("scheduleId") @Min(1) int scheduleId,
                                    @PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        scheduleService.removeDeparture(scheduleId, stationCodeId);
        return Response.noContent().build();
    }

    private Departure unwrapDeparture(DepartureRefSpec departureRefSpec) {
        Station station = stationService.fetchStation(departureRefSpec.getStationCodeId());
        LocalTime time = departureRefSpec.getTime();

        return Departure.of(station, time);
    }

    private Collection<Departure> unwrapDepartures(Collection<DepartureRefSpec> departureDTOs) {
        return departureDTOs.stream().map(this::unwrapDeparture).collect(Collectors.toList());
    }
}
