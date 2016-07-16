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

import eu.socialedge.hermes.application.resource.exception.BadRequestException;
import eu.socialedge.hermes.application.resource.exception.NotFoundException;
import eu.socialedge.hermes.application.resource.ext.PATCH;
import eu.socialedge.hermes.application.resource.ext.Resource;
import eu.socialedge.hermes.domain.infrastructure.Route;
import eu.socialedge.hermes.domain.infrastructure.RouteRepository;
import eu.socialedge.hermes.domain.infrastructure.Station;
import eu.socialedge.hermes.domain.infrastructure.StationRepository;
import eu.socialedge.hermes.domain.timetable.Departure;
import eu.socialedge.hermes.domain.timetable.Schedule;
import eu.socialedge.hermes.domain.timetable.ScheduleRepository;
import org.apache.commons.lang3.StringUtils;

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

@Resource
@Path("/v1/schedules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ScheduleResource {
    @Inject private ScheduleRepository scheduleRepository;
    @Inject private RouteRepository routeRepository;
    @Inject private StationRepository stationRepository;

    @POST
    public Response create(@NotNull @Valid SchedulePatch schedulePatch, @Context UriInfo uriInfo) {
        Route route = routeRepository.get(schedulePatch.getRouteCodeId()).orElseThrow(()
                -> new NotFoundException("No route found with code id = " + schedulePatch.getRouteCodeId()));

        String scheduleName = schedulePatch.getName();
        Set<Departure> scheduleDeps = schedulePatch.getDepartures();
        LocalDate expDate = schedulePatch.getExpirationDate();

        Schedule schedule = scheduleDeps != null && scheduleDeps.size() > 0 ?
                                new Schedule(route, scheduleName, scheduleDeps) :
                                    new Schedule(route, scheduleName);

        if (expDate != null) {
            if (expDate.isBefore(LocalDate.now()))
                throw new BadRequestException("expiration date cant be before today's date");
            schedule.setExpirationDate(expDate);
        }

        Schedule persistedSchedule = scheduleRepository.store(schedule);
        return Response.created(uriInfo.getAbsolutePathBuilder()
                                       .path(String.valueOf(persistedSchedule.getScheduleId()))
                                       .build())
                       .build();
    }

    @POST
    @Path("/{scheduleId}/departures")
    public Response createDeparture(@NotNull @Valid DepartureDefinition depDef,
                                            @PathParam("routeCodeId") @Size(min = 1) String routeCodeId,
                                            @PathParam("scheduleId") @Min(1) int scheduleId) {
        Schedule schedule = scheduleRepository.get(scheduleId).orElseThrow(()
                -> new NotFoundException("No schedule found with id = " + scheduleId));
        Station station = stationRepository.get(depDef.getStationCodeId()).orElseThrow(() ->
                new NotFoundException("Cannot find station with code id = " + depDef.getStationCodeId()));

        try {
            schedule.addDeparture(Departure.of(station, depDef.getTime()));
            scheduleRepository.store(schedule);
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    @GET
    public Collection<Schedule> read(@QueryParam("routeCodeId") String routeCodeId) {
        if (StringUtils.isBlank(routeCodeId))
            return scheduleRepository.list();

        return scheduleRepository.findByRouteCodeId(routeCodeId);
    }

    @GET
    @Path("/{scheduleId}")
    public Schedule read(@PathParam("scheduleId") @Min(1) int scheduleId) {
        return scheduleRepository.get(scheduleId).orElseThrow(()
                -> new NotFoundException("No schedule found with id = " + scheduleId));
    }

    @GET
    @Path("/{scheduleId}/departures")
    public Set<Departure> readDepatures(@PathParam("scheduleId") @Min(1) int scheduleId) {
        return read(scheduleId).getDepartures();
    }

    @PATCH
    @Path("/{scheduleId}")
    public Response update(@NotNull SchedulePatch schedulePatch,
                           @PathParam("scheduleId") @Min(1) int scheduleId) {
        Schedule scheduleToPatch = read(scheduleId);

        if (StringUtils.isNotBlank(schedulePatch.getName()))
            scheduleToPatch.setName(schedulePatch.getName());

        LocalDate patchExpirationDate = schedulePatch.getExpirationDate();
        if (patchExpirationDate != null && patchExpirationDate.isBefore(LocalDate.now()))
            scheduleToPatch.setExpirationDate(patchExpirationDate);

        Set<Departure> patchDepartures = schedulePatch.getDepartures();
        if (patchDepartures != null && !patchDepartures.isEmpty())
            scheduleToPatch.setDepartures(patchDepartures);

        scheduleRepository.store(scheduleToPatch);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{scheduleId}")
    public Response delete(@PathParam("scheduleId") @Min(1) int scheduleId) {
        scheduleRepository.remove(read(scheduleId));
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{scheduleId}/departures/{stationCodeId}")
    public Response deleteDeparture(@PathParam("scheduleId") @Min(1) int scheduleId,
                                    @PathParam("stationCodeId") @Size(min = 1) String stationCodeId) {
        Schedule schedule = read(scheduleId);
        Departure depToRemove = schedule.getDepartures().stream()
                                        .filter(s -> s.getStation().getStationCodeId()
                                                                   .equalsIgnoreCase(stationCodeId))
                                        .findFirst().orElseThrow(()
                                            -> new NotFoundException("No station on the route found " +
                                                "with code id = " + stationCodeId));

        schedule.removeDeparture(depToRemove);
        scheduleRepository.store(schedule);
        return Response.noContent().build();
    }

    private static class SchedulePatch {
        @NotNull
        @Size(min = 1)
        private String routeCodeId;

        @NotNull
        @Size(min = 1)
        private String name;

        private Set<Departure> departures;
        private LocalDate expirationDate;

        public String getRouteCodeId() {
            return routeCodeId;
        }

        public void setRouteCodeId(String routeCodeId) {
            this.routeCodeId = routeCodeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<Departure> getDepartures() {
            return departures;
        }

        public void setDepartures(Set<Departure> departures) {
            this.departures = departures;
        }

        public LocalDate getExpirationDate() {
            return expirationDate;
        }

        public void setExpirationDate(LocalDate expirationDate) {
            this.expirationDate = expirationDate;
        }
    }

    private static class DepartureDefinition {
        @NotNull
        @Size(min = 1)
        private String stationCodeId;

        @NotNull
        private LocalTime time;

        public String getStationCodeId() {
            return stationCodeId;
        }

        public void setStationCodeId(String stationCodeId) {
            this.stationCodeId = stationCodeId;
        }

        public LocalTime getTime() {
            return time;
        }

        public void setTime(LocalTime time) {
            this.time = time;
        }
    }
}
