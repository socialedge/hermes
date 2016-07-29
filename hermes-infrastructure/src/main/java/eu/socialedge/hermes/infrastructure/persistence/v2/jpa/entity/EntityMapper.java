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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.entity;

import eu.socialedge.hermes.domain.v2.infrastructure.Station;
import eu.socialedge.hermes.domain.v2.infrastructure.TransportType;
import eu.socialedge.hermes.domain.v2.operator.Agency;
import eu.socialedge.hermes.domain.v2.operator.Email;
import eu.socialedge.hermes.domain.v2.operator.Location;
import eu.socialedge.hermes.domain.v2.operator.Phone;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneOffset;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class EntityMapper {

    public static JpaAgency mapAgencyToEntity(Agency agency) {
        JpaAgency jpaAgency = new JpaAgency();

        jpaAgency.agencyId(agency.agencyId().toString());

        jpaAgency.name(agency.name());
        jpaAgency.website(agency.website().toString());

        jpaAgency.timeZone(agency.timeZoneOffset().getTotalSeconds());
        jpaAgency.location(mapLocationToEntity(agency.location()));

        if (!isNull(agency.phone()))
            jpaAgency.phone(agency.phone().toString());
        if (!isNull(agency.email()))
            jpaAgency.email(agency.email().toString());

        return jpaAgency;
    }

    public static Agency mapEntityToAgency(JpaAgency jpaAgency) throws MalformedURLException {
        String agencyId = jpaAgency.agencyId();

        String name = jpaAgency.name();
        URL website = new URL(jpaAgency.website());

        ZoneOffset zoneOffset = ZoneOffset.ofTotalSeconds(jpaAgency.timeZone());
        Location location = mapEntityToLocation(jpaAgency.location());

        Agency agency = new Agency(agencyId, name, website, zoneOffset, location);

        if (!isBlank(jpaAgency.email()))
            agency.email(new Email(jpaAgency.email()));
        if (!isBlank(jpaAgency.phone()))
            agency.phone(new Phone(jpaAgency.phone()));

        return agency;
    }

    public static JpaStation mapStationToEntity(Station station) {
        JpaStation jpaStation = new JpaStation();

        jpaStation.stationId(station.stationId().toString());
        jpaStation.name(station.name());

        jpaStation.location(mapLocationToEntity(station.location()));
        jpaStation.transportTypes(station.transportTypes());

        return jpaStation;
    }

    public static Station mapEntityToStation(JpaStation jpaStation) {
        String stationId = jpaStation.stationId();
        String name = jpaStation.name();
        Location location = mapEntityToLocation(jpaStation.location());
        Set<TransportType> transportTypes = jpaStation.transportTypes();

        return new Station(stationId, name, location, transportTypes);
    }

    private static JpaLocation mapLocationToEntity(Location location) {
        JpaLocation jpaLocation = new JpaLocation();

        jpaLocation.latitude(location.latitude());
        jpaLocation.longitude(location.longitude());

        return jpaLocation;
    }

    private static Location mapEntityToLocation(JpaLocation jpaLocation) {
        return Location.of(jpaLocation.latitude(), jpaLocation.longitude());
    }
}
