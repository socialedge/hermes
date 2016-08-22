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
package eu.socialedge.hermes.domain.infrastructure;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import eu.socialedge.hermes.application.HermesApplication;
import eu.socialedge.hermes.application.domain.infrastructure.InfrastructureService;
import eu.socialedge.hermes.domain.TestDatabaseConfig;
import eu.socialedge.hermes.domain.TestDatabaseInitializer;
import eu.socialedge.hermes.domain.geo.dto.LocationSpecification;
import eu.socialedge.hermes.domain.infrastructure.dto.StationSpecification;
import eu.socialedge.hermes.domain.infrastructure.dto.StationSpecificationMapper;
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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static eu.socialedge.hermes.domain.TestDatabaseInitializer.*;

@WebIntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {HermesApplication.class, TestDatabaseConfig.class})
public class InfrastructureResourceIntegrationTest {

    private WebResource resource;

    @Inject
    private InfrastructureService infrastructureService;

    @Inject
    private StationRepository stationRepository;


    @Inject
    private TestDatabaseInitializer initializer;

    @Inject
    private StationSpecificationMapper stationMapper;

    private Gson gson = new Gson();
    private final String url = "http://localhost:9999/api/v1.2/stations/";

    @Before
    public void setUp() throws Exception {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(URLConnectionClientHandler.PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND, true);
        Client client = Client.create(config);

        resource = client.resource(url);

        initializer.initialize();
    }

    @Test
    public void testGetOneShouldRespondWithStatusOkAndCorrectStation() throws Exception {
        String stationId = STATION_ID_BASE + 1;
        ClientResponse response = resource.path(stationId)
                .get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String jsonResult = response.getEntity(String.class);
        StationSpecification resultSpec = gson.fromJson(jsonResult, StationSpecification.class);

        Station station = infrastructureService.fetchStation(StationId.of(stationId));

        assertEquals(stationMapper.toDto(station), resultSpec);
    }

    @Test
    public void testGetOneShouldThrowExceptionForWrongStationId() {
        ClientResponse response = resource.path("-1").get(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testGetAllShouldRespondWithStatusOkAndReturnAllAgencies() throws Exception {
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String resultJsonString = response.getEntity(String.class);
        JsonArray resultJsonArray = gson.fromJson(resultJsonString, JsonArray.class);

        List<StationSpecification> resultSpecifications = new ArrayList<>();
        for (JsonElement object: resultJsonArray) {
            resultSpecifications.add(gson.fromJson(object, StationSpecification.class));
        }

        assertEquals(infrastructureService.fetchAllStations().stream().map(stationMapper::toDto).collect(Collectors.toList()),
                resultSpecifications);
    }

    @Test
    public void testCreateShouldPersistStationAndRespondWithStatusCreated() {
        StationSpecification specification = new StationSpecification();
        specification.id = STATION_ID_BASE + (EACH_ENTITY_COUNT + 1);
        specification.name = "name";
        specification.location = new LocationSpecification();
        specification.location.latitude = 10f;
        specification.location.longitude = 10f;
        specification.vehicleTypes = new HashSet<String>() {{
            add(VehicleType.BUS.name());
        }};

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(url + specification.id, response.getHeaders().getFirst("location"));

        assertTrue(stationRepository.contains(StationId.of(specification.id)));
    }

    @Test
    public void testCreateShouldRespondWithBadRequestForNullEntityInput() {
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testCreateShouldRespondWithBadRequestForInvalidEntityInput() {
        StationSpecification specification = new StationSpecification();
        specification.name = "";

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testUpdateStationShouldRespondWithStatusOkAndUpdateTheEntity() {
        StationSpecification specification = stationMapper.toDto(infrastructureService.fetchStation(StationId.of(STATION_ID_BASE+ 1)));
        specification.name = "newName";
        specification.vehicleTypes = new HashSet<String>() {{
           add(VehicleType.TRAM.name());
        }};
        specification.location.latitude = 11f;
        specification.location.longitude = 11f;

        ClientResponse response = resource.path(specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        Station station = infrastructureService.fetchStation(StationId.of(specification.id));

        assertEquals(specification.id, station.id().toString());
        assertEquals(specification.name, station.name());
        assertEquals(specification.location.latitude, station.location().latitude(), 0.0);
        assertEquals(specification.location.longitude, station.location().longitude(), 0.0);
        assertEquals(specification.vehicleTypes, station.vehicleTypes().stream()
                .map(VehicleType::name)
                .collect(Collectors.toSet()));
    }

    @Test
    public void testUpdateShouldRespondWithBadRequestForNullEntity() {
        ClientResponse response = resource.path("someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testUpdateShouldRespondWithBadRequestForInvalidEntity() {
        StationSpecification specification = stationMapper.toDto(infrastructureService.fetchStation(StationId.of(STATION_ID_BASE+ 1)));
        specification.name = "";

        ClientResponse response = resource.path(specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testUpdateShouldRespondWithNotFoundForWrongId() {
        StationSpecification specification = stationMapper.toDto(infrastructureService.fetchStation(StationId.of(STATION_ID_BASE+ 1)));

        ClientResponse response = resource.path("someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDeleteShouldRespondWithNoContentAndDeleteFromDatabase() {
        StationSpecification specifications = stationMapper.toDto(infrastructureService.fetchStation(StationId.of(STATION_ID_BASE+ 1)));

        ClientResponse response = resource.path(specifications.id).delete(ClientResponse.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

        assertFalse(stationRepository.contains(StationId.of(specifications.id)));
    }

    @Test
    public void testDeleteShouldRespondWithNotFoundForWrongId() {
        ClientResponse response = resource.path("someId").delete(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @After
    public void tearDown() {
        initializer.clear();
    }
}
