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
import eu.socialedge.hermes.application.resource.exception.BadRequestException;
import eu.socialedge.hermes.application.resource.exception.NotFoundException;
import eu.socialedge.hermes.domain.infrastructure.Route;
import eu.socialedge.hermes.domain.infrastructure.RouteRepository;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.timetable.Departure;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

import static eu.socialedge.hermes.application.resource.dto.DTOMapper.departureResponse;
import static eu.socialedge.hermes.application.resource.dto.DTOMapper.scheduleResponse;

@Resource
@Path("/v1/schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional(readOnly = true)
public class ScheduleResource {
    @Inject private ScheduleRepository scheduleRepository;
    @Inject private RouteRepository routeRepository;
    @Inject private StationRepository stationRepository;

    @POST
    @Transactional
    public Response create(@NotNull @Valid ScheduleDTO scheduleDTO, @Context UriInfo uriInfo) {
        String name = scheduleDTO.getName();
        Set<DepartureDTO> departureDefs = scheduleDTO.getDepartures();
        LocalDate expDate = scheduleDTO.getExpirationDate();

        Route route = fetchRoute(scheduleDTO.getRouteCodeId());

        Schedule schedule;
        if (departureDefs != null && !departureDefs.isEmpty()) {
            Collection<Departure> departures = unwrapDepartures(departureDefs);
            schedule = new Schedule(route, name, departures);
        } else {
            schedule = new Schedule(route, name);
        }

        if (expDate != null) {
            if (expDate.isBefore(LocalDate.now()))
                throw new BadRequestException("expiration date cant be before today's date");
            schedule.setExpirationDate(expDate);
        }

        Schedule persistedSchedule = scheduleRepository.store(schedule);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(persistedSchedule.getId()))
                .build()).build();
    }

    @POST
    @Transactional
    @Path("/{scheduleId}/departures")
    public Response createDeparture(@NotNull @Valid DepartureDTO departureDTO,
                                            @PathParam("routeCodeId") @Size(min = 1) String routeCodeId,
                                            @PathParam("scheduleId") @Min(1) int scheduleId) {
        Schedule schedule = fetchSchedule(scheduleId);
        Station station = fetchStation(departureDTO.getStationCodeId());

        try {
            schedule.addDeparture(Departure.of(station, departureDTO.getTime()));
            scheduleRepository.store(schedule);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @GET
    public Collection<ScheduleDTO> read(@QueryParam("routeCodeId") String routeCodeId) {
        if (StringUtils.isBlank(routeCodeId))
            return scheduleResponse(scheduleRepository.list());

        return scheduleResponse(scheduleRepository.findByRouteCodeId(routeCodeId));
    }

    @GET
    @Path("/{scheduleId}")
    public ScheduleDTO read(@PathParam("scheduleId") @Min(1) int scheduleId) {
        return scheduleResponse(fetchSchedule(scheduleId));
    }

    @GET
    @Path("/{scheduleId}/departures")
    public Collection<DepartureDTO> readDepatures(@PathParam("scheduleId") @Min(1) int scheduleId) {
        return departureResponse(fetchSchedule(scheduleId).getDepartures());
    }

    @PATCH
    @Transactional
    @Path("/{scheduleId}")
    public Response update(@NotNull ScheduleDTO scheduleDTO,
                           @PathParam("scheduleId") @Min(1) int scheduleId) {
        Schedule scheduleToPatch = fetchSchedule(scheduleId);
        boolean wasUpdated = false;

        String name = scheduleDTO.getName();
        if (StringUtils.isNotBlank(name)) {
            scheduleToPatch.setName(name);
            wasUpdated = true;
        }

        LocalDate expDate = scheduleDTO.getExpirationDate();
        if (expDate != null && expDate.isBefore(LocalDate.now())) {
            scheduleToPatch.setExpirationDate(expDate);
            wasUpdated = true;
        }

        Set<DepartureDTO> departuresDefs = scheduleDTO.getDepartures();
        if (departuresDefs != null && !departuresDefs.isEmpty()) {
            Collection<Departure> departures = unwrapDepartures(departuresDefs);
            scheduleToPatch.setDepartures(departures);
            wasUpdated = true;
        }

        if (wasUpdated)
            scheduleRepository.store(scheduleToPatch);

        return Response.ok().build();
    }

    @DELETE
    @Transactional
    @Path("/{scheduleId}")
    public Response delete(@PathParam("scheduleId") @Min(1) int scheduleId) {
        scheduleRepository.remove(fetchSchedule(scheduleId));
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{scheduleId}/departures/{stationCodeId}")
    public Response deleteDeparture(@PathParam("scheduleId") @Min(1) int scheduleId,
                                    @PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        Schedule schedule = fetchSchedule(scheduleId);

        if (!schedule.removeDeparture(stationCodeId))
            throw new NotFoundException("No station on the route found with code id = " + stationCodeId);

        scheduleRepository.store(schedule);
        return Response.noContent().build();
    }


    private Schedule fetchSchedule(int scheduleId) {
        return scheduleRepository.get(scheduleId).orElseThrow(()
                -> new NotFoundException("No schedule found with id = " + scheduleId));
    }

    private Route fetchRoute(String routeCodeId) {
        return routeRepository.get(routeCodeId).orElseThrow(()
                -> new NotFoundException("No route found with code id = " + routeCodeId));
    }

    private Station fetchStation(String stationCodeId) {
        return stationRepository.get(stationCodeId).orElseThrow(()
                ->  new NotFoundException("No station found with code id = " + stationCodeId));
    }

    private Departure unwrapDeparture(DepartureDTO departureDTO) {
        Station station = fetchStation(departureDTO.getStationCodeId());
        LocalTime time = departureDTO.getTime();

        return Departure.of(station, time);
    }

    private Collection<Departure> unwrapDepartures(Collection<DepartureDTO> departureDTOs) {
        return departureDTOs.stream().map(this::unwrapDeparture).collect(Collectors.toList());
    }
}
