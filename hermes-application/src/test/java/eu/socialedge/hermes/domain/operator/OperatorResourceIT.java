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
package eu.socialedge.hermes.domain.operator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import eu.socialedge.hermes.application.HermesApplication;
import eu.socialedge.hermes.application.domain.operator.OperatorService;
import eu.socialedge.hermes.domain.TestDatabaseConfig;
import eu.socialedge.hermes.domain.TestDatabaseInitializer;
import eu.socialedge.hermes.domain.geo.dto.LocationSpecification;
import eu.socialedge.hermes.domain.operator.dto.AgencySpecification;
import eu.socialedge.hermes.domain.operator.dto.AgencySpecificationMapper;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static eu.socialedge.hermes.domain.TestDatabaseInitializer.*;

@WebIntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {HermesApplication.class, TestDatabaseConfig.class})
public class OperatorResourceIT {

    private WebResource resource;

    @Inject
    private OperatorService operatorService;

    @Inject
    private AgencyRepository agencyRepository;

    @Inject
    private TestDatabaseInitializer initializer;

    @Inject
    private AgencySpecificationMapper agencyMapper;

    private Gson gson = new Gson();
    private final String url = "http://localhost:9999/api/v1.2/agencies/";

    @Before
    public void setUp() throws Exception {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(URLConnectionClientHandler.PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND, true);
        Client client = Client.create(config);

        resource = client.resource(url);

        initializer.initialize();
    }

    @Test
    public void testGetOneShouldRespondWithStatusOkAndCorrectAgency() throws Exception {
        String agencyId = AGENCY_ID_BASE + 1;
        ClientResponse response = resource.path(agencyId).get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String jsonResult = response.getEntity(String.class);
        AgencySpecification resultSpec = gson.fromJson(jsonResult, AgencySpecification.class);

        Agency agency = operatorService.fetchAgency(AgencyId.of(agencyId));

        assertEquals(agencyMapper.toDto(agency), resultSpec);
    }

    @Test
    public void testGetOneShouldThrowExceptionForWrongAgencyId() {
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

        List<AgencySpecification> resultSpecifications = new ArrayList<>();
        for (JsonElement object: resultJsonArray) {
            resultSpecifications.add(gson.fromJson(object, AgencySpecification.class));
        }

        assertEquals(operatorService.fetchAllAgencies().stream().map(agencyMapper::toDto).collect(Collectors.toList()),
                resultSpecifications);
    }

    @Test
    public void testCreateShouldPersistAgencyAndRespondWithStatusCreated() {
        AgencySpecification specification = new AgencySpecification();
        specification.id = AGENCY_ID_BASE + (EACH_ENTITY_COUNT + 1);
        specification.name = "name";
        specification.email = "email@mail.ru";
        specification.location = new LocationSpecification();
        specification.location.latitude = 10f;
        specification.location.longitude = 10f;
        specification.phone = "+1 1111111111";
        specification.website = "http://google.com";
        specification.timeZoneOffset = "+18:00";

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(url + specification.id, response.getHeaders().getFirst("location"));

        assertTrue(agencyRepository.contains(AgencyId.of(specification.id)));
    }

    @Test
    public void testCreateShouldRespondWithBadRequestForNullEntityInput() {
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testCreateShouldRespondWithBadRequestForInvalidEntityInput() {
        AgencySpecification specification = new AgencySpecification();
        specification.name = "";

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testUpdateAgencyShouldRespondWithStatusOkAndUpdateTheEntity() {
        AgencySpecification specification = agencyMapper.toDto(operatorService.fetchAgency(AgencyId.of(AGENCY_ID_BASE + 1)));
        specification.name = "newName";
        specification.email = "newMail@mail.ru";
        specification.phone = "+1 1111111111111";
        specification.website = "https://secure.com";
        specification.timeZoneOffset = "-18:00";
        specification.location.latitude = 11f;
        specification.location.longitude = 11f;

        ClientResponse response = resource.path(specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        Agency agency = operatorService.fetchAgency(AgencyId.of(specification.id));

        assertEquals(specification.id, agency.id().toString());
        assertEquals(specification.name, agency.name());
        assertEquals(specification.email, agency.email().address());
        assertEquals(specification.phone, agency.phone().number());
        assertEquals(specification.timeZoneOffset, agency.timeZone().toString());
        assertEquals(specification.location.latitude, agency.location().latitude(), 0.0);
        assertEquals(specification.location.longitude, agency.location().longitude(), 0.0);
        assertEquals(specification.website, agency.website().toString());
    }

    @Test
    public void testUpdateShouldRespondWithBadRequestForNullEntity() {
        ClientResponse response = resource.path("someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testUpdateShouldRespondWithBadRequestForInvalidEntity() {
        AgencySpecification specification = agencyMapper.toDto(operatorService.fetchAgency(AgencyId.of(AGENCY_ID_BASE + 1)));
        specification.name = "";

        ClientResponse response = resource.path(specification.id).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testUpdateShouldRespondWithNotFoundForWrongId() {
        AgencySpecification specification = agencyMapper.toDto(operatorService.fetchAgency(AgencyId.of(AGENCY_ID_BASE + 1)));

        ClientResponse response = resource.path("someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void testDeleteShouldRespondWithNoContentAndDeleteFromDatabase() {
        AgencySpecification specifications = agencyMapper.toDto(operatorService.fetchAgency(AgencyId.of(AGENCY_ID_BASE + 1)));

        ClientResponse response = resource.path(specifications.id).delete(ClientResponse.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());

        assertFalse(agencyRepository.contains(AgencyId.of(specifications.id)));
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
