package eu.socialedge.hermes.backend.application.api;

import org.junit.Test;

import static org.junit.Assert.*;

public class EndpointsTest {

    @Test
    public void matchesUrlCorrectly() {
        assertTrue(Endpoints.AGENCY.matches("https://backend.com/agencies"));
        assertTrue(Endpoints.AGENCY.matches("https://backend.com/agenCIes"));
        assertTrue(Endpoints.AGENCY.matches("https://backend.com/AGENCIES"));
        assertTrue(Endpoints.AGENCY.matches("backend.com/agencies"));
        assertTrue(Endpoints.AGENCY.matches("/agencies"));
        assertTrue(Endpoints.AGENCY.matches("/agencies/"));
        assertTrue(Endpoints.AGENCY.matches("/agencies/12312"));
        assertTrue(Endpoints.AGENCY.matches("/agencies/12312?var=true"));
        assertTrue(Endpoints.AGENCY.matches("/agencies/12312?var=true&whatever"));

        assertFalse(Endpoints.AGENCY.matches("backend.com/agenciess"));
        assertFalse(Endpoints.AGENCY.matches("backend.com/agenciess"));
        assertFalse(Endpoints.AGENCY.matches("agencies"));
    }

    @Test
    public void parsesIdCorrectly() {
        assertFalse(Endpoints.AGENCY.parseId("https://backend.com/agencies").isPresent());
        assertFalse(Endpoints.AGENCY.parseId("https://backend.com/AGENCIES/").isPresent());
        assertFalse(Endpoints.AGENCY.parseId("https://backend.com/AGENCIES?notid").isPresent());

        assertEquals("12312", Endpoints.AGENCY.parseId("/agencies/12312").get());
        assertEquals("12312", Endpoints.AGENCY.parseId("/agencies/12312/").get());
        assertEquals("12312&ADASDASD", Endpoints.AGENCY.parseId("/agencies/12312&ADASDASD").get());
        assertEquals("12312", Endpoints.AGENCY.parseId("/agencies/12312?notid&againnotid").get());
        assertEquals("12312", Endpoints.AGENCY.parseId("/agencies/12312?notid").get());
    }
}
