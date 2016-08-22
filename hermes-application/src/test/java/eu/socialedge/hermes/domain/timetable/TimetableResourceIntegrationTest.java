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
package eu.socialedge.hermes.domain.timetable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import eu.socialedge.hermes.application.HermesApplication;
import eu.socialedge.hermes.application.domain.timetable.TimetableService;
import eu.socialedge.hermes.domain.TestDatabaseConfig;
import eu.socialedge.hermes.domain.TestDatabaseInitializer;
import eu.socialedge.hermes.domain.timetable.dto.*;
import eu.socialedge.hermes.domain.transit.RouteId;
import eu.socialedge.hermes.domain.transit.RouteRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static eu.socialedge.hermes.domain.TestDatabaseInitializer.*;

@WebIntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {HermesApplication.class, TestDatabaseConfig.class})
public class TimetableResourceIntegrationTest {

    private WebResource resource;

    @Inject
    private TestDatabaseInitializer initializer;

    @Inject
    private ScheduleSpecificationMapper scheduleMapper;

    @Inject
    private TripSpecificationMapper tripMapper;

    @Inject
    private TimetableService timetableService;

    @Inject
    private RouteRepository routeRepository;

    @Inject
    private TripRepository tripRepository;

    @Inject
    private ScheduleRepository scheduleRepository;

    private Gson gson = new Gson();
    private final String baseUrl = "http://localhost:9999/api/v1.2/schedules/";
    private final String tripResourceUrl = "/trips/";

    @Before
    public void setUp() throws Exception {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(URLConnectionClientHandler.PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND, true);
        Client client = Client.create(config);
        resource = client.resource(baseUrl);

        initializer.initialize();
    }

    @Test
    public void testScheduleGetOneShouldRespondWithStatusOkAndCorrectSchedule() throws Exception {
        String scheduleId = SCHEDULE_ID_BASE + 1;

        ClientResponse response = resource.path(scheduleId).get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String jsonResult = response.getEntity(String.class);
        ScheduleSpecification resultSpec = gson.fromJson(jsonResult, ScheduleSpecification.class);

        Schedule schedule = timetableService.fetchSchedule(ScheduleId.of(scheduleId));
        assertEquals(scheduleMapper.toDto(schedule), resultSpec);
    }

    @Test
    public void testScheduleGetOneShouldThrowExceptionForWrongScheduleId() {
        ClientResponse response = resource.path("-1").get(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testScheduleGetAllShouldRespondWithStatusOkAndReturnAllAgencies() throws Exception {
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String resultJsonString = response.getEntity(String.class);
        JsonArray resultJsonArray = gson.fromJson(resultJsonString, JsonArray.class);

        List<ScheduleSpecification> resultSpecifications = new ArrayList<>();
        for (JsonElement object : resultJsonArray) {
            resultSpecifications.add(gson.fromJson(object, ScheduleSpecification.class));
        }

        assertEquals(timetableService.fetchAllSchedules().stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList()), resultSpecifications);
    }

    @Test
    public void testScheduleGetAllByRouteIdShouldRespondWithStatusOkAndReturnAllAgencies() throws Exception {
        RouteId routeId = RouteId.of(ROUTE_ID_BASE + 1);
        ClientResponse response = resource.queryParam("routeId", routeId.toString()).get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String resultJsonString = response.getEntity(String.class);
        JsonArray resultJsonArray = gson.fromJson(resultJsonString, JsonArray.class);

        List<ScheduleSpecification> resultSpecifications = new ArrayList<>();
        for (JsonElement object : resultJsonArray) {
            resultSpecifications.add(gson.fromJson(object, ScheduleSpecification.class));
        }

        assertEquals(timetableService.fetchAllSchedulesByRouteId(routeId).stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList()), resultSpecifications);
    }

    @Test
    public void testScheduleCreateShouldPersistScheduleAndRespondWithStatusCreated() {
        ScheduleSpecification specification = new ScheduleSpecification();
        specification.id = SCHEDULE_ID_BASE + (EACH_ENTITY_COUNT + 1);
        specification.name = "schedule";
        specification.routeId = ROUTE_ID_BASE + 1;
        specification.scheduleAvailability = ScheduleAvailability.weekendDays(LocalDate.now().minusDays(1), LocalDate.now());
        specification.tripIds = new HashSet<String>() {{
            add(TRIP_ID_BASE + 1);
        }};

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(baseUrl + specification.id, response.getHeaders().getFirst("location"));

        assertTrue(scheduleRepository.contains(ScheduleId.of(specification.id)));
    }

    @Test
    public void testScheduleCreateShouldRespondWithBadRequestForNullEntityInput() {
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testScheduleCreateShouldRespondWithBadRequestForInvalidEntityInput() {
        ScheduleSpecification specification = scheduleMapper.toDto(initializer.addSchedule(TestDatabaseInitializer.EACH_ENTITY_COUNT));
        specification.name = "";

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testScheduleUpdateScheduleShouldRespondWithStatusOkAndUpdateTheEntity() {
        ScheduleId scheduleId = ScheduleId.of(SCHEDULE_ID_BASE + 1);
        ScheduleSpecification specification = scheduleMapper.toDto(timetableService.fetchSchedule(scheduleId));
        specification.name = "newName";
        specification.tripIds = new HashSet<String>() {{
            add(TRIP_ID_BASE + 2);
        }};

        ClientResponse response = resource.path(specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        Schedule schedule = timetableService.fetchSchedule(scheduleId);

        assertEquals(specification.id, scheduleId.toString());
        assertEquals(specification.name, schedule.name());
    }

    @Test
    public void testScheduleUpdateShouldRespondWithBadRequestForNullEntity() {
        ClientResponse response = resource.path("someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testScheduleUpdateShouldRespondWithBadRequestForInvalidEntity() {
        ScheduleId scheduleId = ScheduleId.of(SCHEDULE_ID_BASE + 1);
        ScheduleSpecification specification = scheduleMapper.toDto(timetableService.fetchSchedule(scheduleId));
        specification.name = "";

        ClientResponse response = resource.path(specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testScheduleUpdateShouldRespondWithNotFoundForWrongId() {
        ScheduleId scheduleId = ScheduleId.of(SCHEDULE_ID_BASE + 1);
        ScheduleSpecification specification = scheduleMapper.toDto(timetableService.fetchSchedule(scheduleId));

        ClientResponse response = resource.path("someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testScheduleDeleteShouldRespondWithNoContentAndDeleteFromDatabase() {
        ScheduleId scheduleId = ScheduleId.of(SCHEDULE_ID_BASE + 1);

        ClientResponse response = resource.path(scheduleId.toString()).delete(ClientResponse.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

        assertFalse(scheduleRepository.contains(scheduleId));
    }

    @Test
    public void testScheduleDeleteShouldRespondWithNotFoundForWrongId() {
        ClientResponse response = resource.path("someId").delete(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testTripGetOneShouldRespondWithStatusOkAndCorrectTrip() throws Exception {
        String scheduleId = SCHEDULE_ID_BASE + 1;
        String tripId = TestDatabaseInitializer.TRIP_ID_BASE + 1;

        ClientResponse response = resource.path(scheduleId + tripResourceUrl + tripId).get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String jsonResult = response.getEntity(String.class);
        TripSpecification resultSpec = gson.fromJson(jsonResult, TripSpecification.class);

        Trip trip = timetableService.fetchTrip(ScheduleId.of(scheduleId), TripId.of(tripId));
        assertEquals(tripMapper.toDto(trip), resultSpec);
    }

    @Test
    public void testTripGetOneShouldThrowExceptionForWrongTripId() {
        String scheduleId = SCHEDULE_ID_BASE + 1;
        ClientResponse response = resource.path(scheduleId + tripResourceUrl + "-1").get(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testTripGetAllShouldRespondWithStatusOkAndReturnAllAgencies() throws Exception {
        String scheduleId = SCHEDULE_ID_BASE + 1;
        ClientResponse response = resource.path(scheduleId + tripResourceUrl).get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String resultJsonString = response.getEntity(String.class);
        JsonArray resultJsonArray = gson.fromJson(resultJsonString, JsonArray.class);

        List<TripSpecification> resultSpecifications = new ArrayList<>();
        for (JsonElement object : resultJsonArray) {
            resultSpecifications.add(gson.fromJson(object, TripSpecification.class));
        }

        assertEquals(timetableService.fetchAllTrips(ScheduleId.of(scheduleId)).stream()
                .map(tripMapper::toDto)
                .collect(Collectors.toList()), resultSpecifications);
    }

    @Test
    public void testTripCreateShouldPersistTripAndRespondWithStatusCreated() {
        String scheduleId = SCHEDULE_ID_BASE + 1;
        TripSpecification specification = new TripSpecification();
        specification.id = TRIP_ID_BASE + EACH_ENTITY_COUNT + 1;
        specification.stops = new HashSet<StopSpecification>() {{
            StopSpecification stopSpecification = new StopSpecification();
            stopSpecification.stationId = STATION_ID_BASE + 1;
            stopSpecification.arrival = "10:15:30";
            stopSpecification.departure = "10:11:30";
            add(stopSpecification) ;
        }};

        ClientResponse response = resource.path(scheduleId + tripResourceUrl).type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(baseUrl + scheduleId + tripResourceUrl + specification.id, response.getHeaders().getFirst("location"));

        assertTrue(tripRepository.contains(TripId.of(specification.id)));
    }

    @Test
    public void testTripCreateShouldRespondWithBadRequestForNullEntityInput() {
        String scheduleId = SCHEDULE_ID_BASE + 1;
        ClientResponse response = resource.path(scheduleId + tripResourceUrl).type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testTripCreateShouldRespondWithBadRequestForInvalidEntityInput() {
        String scheduleId = SCHEDULE_ID_BASE + 1;
        TripSpecification specification = new TripSpecification();
        specification.stops = null;

        ClientResponse response = resource.path(scheduleId + tripResourceUrl).type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testTripUpdateTripShouldRespondWithStatusOkAndUpdateTheEntity() {
        String scheduleId = SCHEDULE_ID_BASE + 1;
        TripId tripId = TripId.of(TRIP_ID_BASE + 1);
        TripSpecification specification = tripMapper.toDto(timetableService.fetchTrip(ScheduleId.of(scheduleId), tripId));
        specification.stops = new HashSet<StopSpecification>() {{
            StopSpecification stopSpecification = new StopSpecification();
            stopSpecification.stationId = STATION_ID_BASE + 1;
            stopSpecification.arrival = "10:15:30";
            stopSpecification.departure = "10:11:30";
            add(stopSpecification) ;
        }};

        ClientResponse response = resource.path(scheduleId + tripResourceUrl + specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        Trip trip = timetableService.fetchTrip(ScheduleId.of(scheduleId), tripId);

        assertEquals(specification.id, tripId.toString());
        assertEquals(tripMapper.fromDto(specification).stops(), trip.stops());
    }

    @Test
    public void testTripUpdateShouldRespondWithBadRequestForNullEntity() {
        String scheduleId = SCHEDULE_ID_BASE + 1;
        ClientResponse response = resource.path(scheduleId + tripResourceUrl + "someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testTripUpdateShouldRespondWithBadRequestForInvalidEntity() {
        ScheduleId scheduleId = ScheduleId.of(SCHEDULE_ID_BASE + 1);
        TripId tripId = TripId.of(TRIP_ID_BASE + 1);
        TripSpecification specification = tripMapper.toDto(timetableService.fetchTrip(scheduleId, tripId));
        specification.stops = null;

        ClientResponse response = resource.path(scheduleId + tripResourceUrl + specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testTripUpdateShouldRespondWithNotFoundForWrongId() {
        ScheduleId scheduleId = ScheduleId.of(SCHEDULE_ID_BASE + 1);
        TripId tripId = TripId.of(TRIP_ID_BASE + 1);
        TripSpecification specification = tripMapper.toDto(timetableService.fetchTrip(scheduleId, tripId));

        ClientResponse response = resource.path(scheduleId + tripResourceUrl + "someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testTripDeleteShouldRespondWithNoContentAndDeleteFromDatabase() {
        ScheduleId scheduleId = ScheduleId.of(SCHEDULE_ID_BASE + 1);
        TripId tripId = TripId.of(TRIP_ID_BASE + 1);

        ClientResponse response = resource.path(scheduleId + tripResourceUrl + tripId.toString()).delete(ClientResponse.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

        assertFalse(tripRepository.contains(tripId));
    }

    @Test
    public void testTripDeleteShouldRespondWithNotFoundForWrongId() {
        ScheduleId scheduleId = ScheduleId.of(SCHEDULE_ID_BASE + 1);
        ClientResponse response = resource.path(scheduleId + tripResourceUrl + "someId").delete(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @After
    public void tearDown() {
        initializer.clear();
    }
}
