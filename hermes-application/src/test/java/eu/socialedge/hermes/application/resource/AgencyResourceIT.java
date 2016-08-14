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

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import eu.socialedge.hermes.application.HermesApplication;
import eu.socialedge.hermes.application.resource.spec.AgencySpecification;
import eu.socialedge.hermes.application.service.AgencyService;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.operator.Agency;
import eu.socialedge.hermes.domain.operator.AgencyId;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@WebIntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(HermesApplication.class)
public class AgencyResourceIT {

    private WebResource resource;

    @Inject
    private AgencyService agencyService;

    private List<AgencySpecification> agencySpecifications;
    private Gson gson = new Gson();
    private final String url = "http://localhost:9999/api/v1.1/agencies/";

    @Before
    public void setUp() {
        DefaultClientConfig config = new DefaultClientConfig();
        config.getProperties().put(URLConnectionClientHandler.PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND, true);
        Client client = Client.create(config);

        resource = client.resource(url);

        agencySpecifications = Arrays.asList(randomSpecification(), randomSpecification(), randomSpecification());
        agencySpecifications.forEach(agencyService::createAgency);
    }

    @Test
    public void testGetOneShouldRespondWithStatusOkAndCorrectAgency() throws Exception {
        AgencySpecification specification = agencySpecifications.get(0);

        ClientResponse response = resource.path(specification.agencyId)
                .get(ClientResponse.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(response.hasEntity());

        String jsonResult = response.getEntity(String.class);
        AgencyJsonRepresentation resultSpec = gson.fromJson(jsonResult, AgencyJsonRepresentation.class);

        assertSpecificationsEquals(specification, resultSpec);
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

        String resultJson = response.getEntity(String.class);
        //TODO assert equality somehow
    }

    @Test
    public void testCreateShouldPersistAgencyAndRespondWithStatusCreated() {
        AgencySpecification specification = randomSpecification();

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(url + specification.agencyId, response.getHeaders().getFirst("location"));
        assertTrue(agencyService.fetchAgency(AgencyId.of(specification.agencyId)).isPresent());
    }

    @Test
    public void testCreateShouldRespondWithBadRequestForNullEntityInput() {
        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testCreateShouldRespondWithBadRequestForInvalidEntityInput() {
        AgencySpecification specification = randomSpecification();
        specification.name = "";

        ClientResponse response = resource.type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testUpdateAgencyShouldRespondWithStatusOkAndUpdateTheEntity() {
        AgencySpecification specification = agencySpecifications.get(0);
        specification.name = "newName";
        specification.email = "newMail@mail.ru";
        specification.phone = "+1 1111111111111";
        specification.website = "https://secure.com";
        specification.timeZoneOffset = "-18:00";
        specification.locationLatitude = 11f;
        specification.locationLongitude= 11f;

        ClientResponse response = resource.path(specification.agencyId).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        Optional<Agency> agencyOptional = agencyService.fetchAgency(AgencyId.of(specification.agencyId));
        assertTrue(agencyOptional.isPresent());
        Agency agency = agencyOptional.get();

        assertEquals(specification.agencyId, agency.id().toString());
        assertEquals(specification.name, agency.name());
        assertEquals(specification.email, agency.email().toString());
        assertEquals(specification.phone, agency.phone().toString());
        assertEquals(specification.timeZoneOffset, agency.timeZoneOffset().toString());
        assertEquals(specification.locationLatitude, agency.location().latitude(), 0.0);
        assertEquals(specification.locationLongitude, agency.location().longitude(), 0.0);
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
        AgencySpecification specification = agencySpecifications.get(0);
        specification.name = "";

        ClientResponse response = resource.path(specification.agencyId).type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(null));

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    public void testUpdateShouldRespondWithNotFoundForWrongId() {
        AgencySpecification specification = agencySpecifications.get(0);

        ClientResponse response = resource.path("someId").type(MediaType.APPLICATION_JSON_TYPE)
                .method("PATCH", ClientResponse.class, gson.toJson(specification));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }

    @Test
    public void testDeleteShouldRespondWithNoContentAndDeleteFromDatabase() {
        AgencySpecification specifications = agencySpecifications.get(0);

        assertTrue(agencyService.fetchAgency(AgencyId.of(specifications.agencyId)).isPresent());

        ClientResponse response = resource.path(specifications.agencyId).delete(ClientResponse.class);

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertFalse(agencyService.fetchAgency(AgencyId.of(specifications.agencyId)).isPresent());
    }

    @Test
    public void testDeleteShouldRespondWithNotFoundForWrongId() {
        ClientResponse response = resource.path("someId").delete(ClientResponse.class);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @After
    public void tearDown() {
        agencySpecifications.stream()
                .map(agencySpecification -> agencySpecification.agencyId)
                .map(AgencyId::of)
                .forEach(agencyService::deleteAgency);
    }

    private class AgencyJsonRepresentation {
        private String agencyId;
        private String name;
        private String website;
        private String timeZoneOffset;
        private Location location;
        private String phone;
        private String email;
    }

    private void assertSpecificationsEquals(AgencySpecification expected, AgencyJsonRepresentation result) {
        assertEquals(expected.agencyId, result.agencyId);
        assertEquals(expected.name, result.name);
        assertEquals(expected.email, result.email);
        assertEquals(expected.phone, result.phone);
        assertEquals(expected.timeZoneOffset, result.timeZoneOffset);
        assertEquals(expected.locationLatitude, result.location.latitude(), 0.0);
        assertEquals(expected.locationLongitude, result.location.longitude(), 0.0);
        assertEquals(expected.website, result.website);
    }

    private AgencySpecification randomSpecification() {
        AgencySpecification agencySpecification = new AgencySpecification();
        agencySpecification.agencyId = RandomIdGenerator.randomAgencyId();
        agencySpecification.email = "email@gmail.com";
        agencySpecification.name = "name";
        agencySpecification.phone = "+1 1234567891230";
        agencySpecification.timeZoneOffset = "+18:00";
        agencySpecification.locationLongitude = 10f;
        agencySpecification.locationLatitude = 10f;
        agencySpecification.website = "http://google.com";
        return agencySpecification;
    }
}
