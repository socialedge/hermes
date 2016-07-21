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
import eu.socialedge.hermes.application.resource.dto.DepartureDTO;
import eu.socialedge.hermes.application.resource.dto.ScheduleDTO;
import eu.socialedge.hermes.application.service.ScheduleService;
import eu.socialedge.hermes.application.service.StationService;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.timetable.Departure;
import eu.socialedge.hermes.domain.timetable.Schedule;
import org.apache.commons.lang3.StringUtils;
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

import static eu.socialedge.hermes.application.resource.dto.DTOMapper.departureResponse;
import static eu.socialedge.hermes.application.resource.dto.DTOMapper.scheduleResponse;

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
    public Response create(@NotNull @Valid ScheduleDTO scheduleDTO, @Context UriInfo uriInfo) {
        String name = scheduleDTO.getName();
        String routeCode = scheduleDTO.getRouteCodeId();
        Collection<Departure> departures = unwrapDepartures(scheduleDTO.getDepartures());
        LocalDate expDate = scheduleDTO.getExpirationDate();

        Schedule persistedSchedule = scheduleService.createSchedule(name, routeCode, departures, expDate);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(persistedSchedule.getId()))
                .build()).build();
    }

    @POST
    @Transactional
    @Path("/{scheduleId}/departures")
    public Response createDeparture(@NotNull @Valid DepartureDTO departureDTO,
                                    @PathParam("scheduleId") @Min(1) int scheduleId,
                                    @Context UriInfo uriInfo) {
        String stationCode = departureDTO.getStationCodeId();
        LocalTime time = departureDTO.getTime();

        scheduleService.createDeparture(scheduleId, stationCode, time);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(scheduleId))
                .build()).build();
    }

    @GET
    public Collection<ScheduleDTO> read(@QueryParam("routeCodeId") String routeCodeId) {
        if (StringUtils.isBlank(routeCodeId))
            return scheduleResponse(scheduleService.fetchAllSchedules());

        return scheduleResponse(scheduleService.fetchAllSchedulesByRouteCode(routeCodeId));
    }

    @GET
    @Path("/{scheduleId}")
    public ScheduleDTO read(@PathParam("scheduleId") @Min(1) int scheduleId) {
        return scheduleResponse(scheduleService.fetchSchedule(scheduleId));
    }

    @GET
    @Path("/{scheduleId}/departures")
    public Collection<DepartureDTO> readDepartures(@PathParam("scheduleId") @Min(1) int scheduleId) {
        return departureResponse(scheduleService.fetchDepartures(scheduleId));
    }

    @PATCH
    @Transactional
    @Path("/{scheduleId}")
    public Response update(@NotNull ScheduleDTO scheduleDTO,
                           @PathParam("scheduleId") @Min(1) int scheduleId) {

        String name = scheduleDTO.getName();
        Collection<Departure> departures = unwrapDepartures(scheduleDTO.getDepartures());
        LocalDate expDate = scheduleDTO.getExpirationDate();

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

    private Departure unwrapDeparture(DepartureDTO departureDTO) {
        Station station = stationService.fetchStation(departureDTO.getStationCodeId());
        LocalTime time = departureDTO.getTime();

        return Departure.of(station, time);
    }

    private Collection<Departure> unwrapDepartures(Collection<DepartureDTO> departureDTOs) {
        return departureDTOs.stream().map(this::unwrapDeparture).collect(Collectors.toList());
    }
}
