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
package eu.socialedge.hermes.domain.transit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import eu.socialedge.hermes.application.HermesApplication;
import eu.socialedge.hermes.application.domain.transit.TransitService;
import eu.socialedge.hermes.domain.TestDatabaseConfig;
import eu.socialedge.hermes.domain.TestDatabaseInitializer;
import eu.socialedge.hermes.domain.transit.dto.LineSpecification;
import eu.socialedge.hermes.domain.transit.dto.LineSpecificationMapper;
import eu.socialedge.hermes.domain.transit.dto.RouteSpecification;
import eu.socialedge.hermes.domain.transit.dto.RouteSpecificationMapper;
import eu.socialedge.hermes.domain.transport.VehicleType;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static eu.socialedge.hermes.domain.TestDatabaseInitializer.*;
import static org.junit.Assert.*;

@WebIntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {HermesApplication.class, TestDatabaseConfig.class})
public class TransitResourceIntegrationTest {

    private WebResource resource;

    @Inject
    private TestDatabaseInitializer initializer;

    @Inject
    private LineSpecificationMapper lineMapper;

    @Inject
    private RouteSpecificationMapper routeMapper;

    @Inject
    private TransitService transitService;

    @Inject
    private RouteRepository routeRepository;

    @Inject
    private LineRepository lineRepository;

    private Gson gson = new Gson();
    private final String baseUrl = "http://localhost:9999/api/v1.2/lines/";
    private final String routeResourceUrl = "/routes/";

    @Before
    public void setUp() throws Exception {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(URLConnectionClientHandler.PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND, true);
        Client client = Client.create(config);
        resource = client.resource(baseUrl);

        initializer.initialize();
    }

    @Test
    public void testLineGetOneShouldRespondWithStatusOkAndCorrectLine() throws Exception {
        String lineId = LINE_ID_BASE + 1;

        ClientResponse response = resource.path(lineId).get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String jsonResult = response.getEntity(String.class);
        LineSpecification resultSpec = gson.fromJson(jsonResult, LineSpecification.class);

        Line line = transitService.fetchLine(LineId.of(lineId));
        assertEquals(lineMapper.toDto(line), resultSpec);
    }

    @Test
    public void testLineGetOneShouldThrowExceptionForWrongLineId() {
        ClientResponse response = resource.path("-1").get(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testLineGetAllShouldRespondWithStatusOkAndReturnAllAgencies() throws Exception {
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String resultJsonString = response.getEntity(String.class);
        JsonArray resultJsonArray = gson.fromJson(resultJsonString, JsonArray.class);

        List<LineSpecification> resultSpecifications = new ArrayList<>();
        for (JsonElement object : resultJsonArray) {
            resultSpecifications.add(gson.fromJson(object, LineSpecification.class));
        }

        assertEquals(transitService.fetchAllLines().stream()
                .map(lineMapper::toDto)
                .collect(Collectors.toList()), resultSpecifications);
    }

    @Test
    public void testLineCreateShouldPersistLineAndRespondWithStatusCreated() {
        LineSpecification specification = new LineSpecification();
        specification.id = LINE_ID_BASE + (EACH_ENTITY_COUNT + 1);
        specification.name = "line";
        specification.agencyId = AGENCY_ID_BASE + 1;
        specification.vehicleType = VehicleType.BUS.name();
        specification.routeIds = new HashSet<String>() {{
            add(ROUTE_ID_BASE + 1);
        }};

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(baseUrl + specification.id, response.getHeaders().getFirst("location"));

        assertTrue(lineRepository.contains(LineId.of(specification.id)));
    }

    @Test
    public void testLineCreateShouldRespondWithBadRequestForNullEntityInput() {
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testLineCreateShouldRespondWithBadRequestForInvalidEntityInput() {
        LineSpecification specification = lineMapper.toDto(initializer.addLine(TestDatabaseInitializer.EACH_ENTITY_COUNT));
        specification.name = "";

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testLineUpdateLineShouldRespondWithStatusOkAndUpdateTheEntity() {
        LineId lineId = LineId.of(LINE_ID_BASE + 1);
        LineSpecification specification = lineMapper.toDto(transitService.fetchLine(lineId));
        specification.name = "newName";
        specification.routeIds = new HashSet<String>() {{
            add(ROUTE_ID_BASE + 2);
        }};

        ClientResponse response = resource.path(specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        Line line = transitService.fetchLine(lineId);

        assertEquals(specification.id, lineId.toString());
        assertEquals(specification.name, line.name());
    }

    @Test
    public void testLineUpdateShouldRespondWithBadRequestForNullEntity() {
        ClientResponse response = resource.path("someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testLineUpdateShouldRespondWithBadRequestForInvalidEntity() {
        LineId lineId = LineId.of(LINE_ID_BASE + 1);
        LineSpecification specification = lineMapper.toDto(transitService.fetchLine(lineId));
        specification.name = "";

        ClientResponse response = resource.path(specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testLineUpdateShouldRespondWithNotFoundForWrongId() {
        LineId lineId = LineId.of(LINE_ID_BASE + 1);
        LineSpecification specification = lineMapper.toDto(transitService.fetchLine(lineId));

        ClientResponse response = resource.path("someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testLineDeleteShouldRespondWithNoContentAndDeleteFromDatabase() {
        LineId lineId = LineId.of(LINE_ID_BASE + 1);

        ClientResponse response = resource.path(lineId.toString()).delete(ClientResponse.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

        assertFalse(lineRepository.contains(lineId));
    }

    @Test
    public void testLineDeleteShouldRespondWithNotFoundForWrongId() {
        ClientResponse response = resource.path("someId").delete(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testRouteGetOneShouldRespondWithStatusOkAndCorrectRoute() throws Exception {
        String lineId = LINE_ID_BASE + 1;
        String routeId = TestDatabaseInitializer.ROUTE_ID_BASE + 1;

        ClientResponse response = resource.path(lineId + routeResourceUrl + routeId).get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String jsonResult = response.getEntity(String.class);
        RouteSpecification resultSpec = gson.fromJson(jsonResult, RouteSpecification.class);

        Route route = transitService.fetchRoute(LineId.of(lineId), RouteId.of(routeId));
        assertEquals(routeMapper.toDto(route), resultSpec);
    }

    @Test
    public void testRouteGetOneShouldThrowExceptionForWrongRouteId() {
        String lineId = LINE_ID_BASE + 1;
        ClientResponse response = resource.path(lineId + routeResourceUrl + "-1").get(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testRouteGetAllShouldRespondWithStatusOkAndReturnAllAgencies() throws Exception {
        String lineId = LINE_ID_BASE + 1;
        ClientResponse response = resource.path(lineId + routeResourceUrl).get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String resultJsonString = response.getEntity(String.class);
        JsonArray resultJsonArray = gson.fromJson(resultJsonString, JsonArray.class);

        List<RouteSpecification> resultSpecifications = new ArrayList<>();
        for (JsonElement object : resultJsonArray) {
            resultSpecifications.add(gson.fromJson(object, RouteSpecification.class));
        }

        assertEquals(transitService.fetchAllRoutes(LineId.of(lineId)).stream()
                .map(routeMapper::toDto)
                .collect(Collectors.toList()), resultSpecifications);
    }

    @Test
    public void testRouteCreateShouldPersistRouteAndRespondWithStatusCreated() {
        String lineId = LINE_ID_BASE + 1;
        RouteSpecification specification = new RouteSpecification();
        specification.id = ROUTE_ID_BASE + EACH_ENTITY_COUNT + 1;
        specification.stationIds = Arrays.asList(STATION_ID_BASE + 1);

        ClientResponse response = resource.path(lineId + routeResourceUrl).type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(baseUrl + lineId + routeResourceUrl + specification.id, response.getHeaders().getFirst("location"));

        assertTrue(routeRepository.contains(RouteId.of(specification.id)));
    }

    @Test
    public void testRouteCreateShouldRespondWithBadRequestForNullEntityInput() {
        String lineId = LINE_ID_BASE + 1;
        ClientResponse response = resource.path(lineId + routeResourceUrl).type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testRouteCreateShouldRespondWithBadRequestForInvalidEntityInput() {
        String lineId = LINE_ID_BASE + 1;
        RouteSpecification specification = new RouteSpecification();
        specification.stationIds = null;

        ClientResponse response = resource.path(lineId + routeResourceUrl).type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testRouteUpdateRouteShouldRespondWithStatusOkAndUpdateTheEntity() {
        String lineId = LINE_ID_BASE + 1;
        RouteId routeId = RouteId.of(ROUTE_ID_BASE + 1);
        RouteSpecification specification = routeMapper.toDto(transitService.fetchRoute(LineId.of(lineId), routeId));
        specification.stationIds = Arrays.asList(STATION_ID_BASE + 1);

        ClientResponse response = resource.path(lineId + routeResourceUrl + specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        Route route = transitService.fetchRoute(LineId.of(lineId), routeId);

        assertEquals(specification.id, routeId.toString());
        assertEquals(routeMapper.fromDto(specification).stationIds(), route.stationIds());
    }

    @Test
    public void testRouteUpdateShouldRespondWithBadRequestForNullEntity() {
        String lineId = LINE_ID_BASE + 1;
        ClientResponse response = resource.path(lineId + routeResourceUrl + "someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testRouteUpdateShouldRespondWithBadRequestForInvalidEntity() {
        LineId lineId = LineId.of(LINE_ID_BASE + 1);
        RouteId routeId = RouteId.of(ROUTE_ID_BASE + 1);
        RouteSpecification specification = routeMapper.toDto(transitService.fetchRoute(lineId, routeId));
        specification.stationIds = null;

        ClientResponse response = resource.path(lineId + routeResourceUrl + specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testRouteUpdateShouldRespondWithNotFoundForWrongId() {
        LineId lineId = LineId.of(LINE_ID_BASE + 1);
        RouteId routeId = RouteId.of(ROUTE_ID_BASE + 1);
        RouteSpecification specification = routeMapper.toDto(transitService.fetchRoute(lineId, routeId));

        ClientResponse response = resource.path(lineId + routeResourceUrl + "someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testRouteDeleteShouldRespondWithNoContentAndDeleteFromDatabase() {
        LineId lineId = LineId.of(LINE_ID_BASE + 1);
        RouteId routeId = RouteId.of(ROUTE_ID_BASE + 1);

        ClientResponse response = resource.path(lineId + routeResourceUrl + routeId.toString()).delete(ClientResponse.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

        assertFalse(routeRepository.contains(routeId));
    }

    @Test
    public void testRouteDeleteShouldRespondWithNotFoundForWrongId() {
        LineId lineId = LineId.of(LINE_ID_BASE + 1);
        ClientResponse response = resource.path(lineId + routeResourceUrl + "someId").delete(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @After
    public void tearDown() {
        initializer.clear();
    }
}
