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

import eu.socialedge.hermes.domain.contact.Email;
import eu.socialedge.hermes.domain.contact.Phone;
import eu.socialedge.hermes.domain.ext.AggregateRoot;
import eu.socialedge.hermes.domain.geo.Location;
import eu.socialedge.hermes.domain.shared.Identifiable;

import java.net.URL;
import java.time.ZoneOffset;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static eu.socialedge.hermes.util.Strings.requireNotBlank;
import static eu.socialedge.hermes.util.Values.requireNotNull;

/**
 * Represents a public transport company that operates bus/tram/train/trolley
 * services.
 *
 * <p>Agency is uniquely identified by {@link AgencyId} - Legal Entity Identifier.
 * It also has {@link Agency#name}, personal {@link Agency#website}, {@link Agency#timeZone}
 * offset, {{@link Agency#phone} number, {@link Agency#email} and headquarters
 * geographic {@link Location}</p>
 *
 * @see <a href="https://goo.gl/oyo05p">Google Transit APIs
 * > Static Transit > agency.txt File</a>
 */
@AggregateRoot
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id") @ToString
@Entity @Table(name = "agencies")
public class Agency implements Identifiable<AgencyId> {

    @EmbeddedId
    private final AgencyId id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "website", nullable = false)
    private URL website;

    @Column(name = "time_zone", nullable = false)
    private ZoneOffset timeZone;

    @Embedded
    private Location location;

    @Embedded
    private Phone phone;

    @Embedded
    private Email email;

    public Agency(AgencyId id, String name, URL website,
                  ZoneOffset timeZone, Location location) {
        this.id = requireNotNull(id);
        this.name = requireNotBlank(name);
        this.website = requireNotNull(website);
        this.timeZone = requireNotNull(timeZone);
        this.location = requireNotNull(location);
    }

    public Agency(AgencyId id, String name, URL website, ZoneOffset timeZone,
                  Location location, Phone phone, Email email) {
        this(id, name, website, timeZone, location);
        this.phone = phone;
        this.email = email;
    }

    @Override
    public AgencyId id() {
        return id;
    }

    public ZoneOffset timeZone() {
        return timeZone;
    }

    public void timeZone(ZoneOffset timeZone) {
        this.timeZone = requireNotNull(timeZone);
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = requireNotBlank(name);
    }

    public URL website() {
        return website;
    }

    public void website(URL website) {
        this.website = requireNotNull(website);
    }

    public Location location() {
        return location;
    }

    public void location(Location location) {
        this.location = requireNotNull(location);
    }

    public Phone phone() {
        return phone;
    }

    public void phone(Phone phone) {
        this.phone = phone;
    }

    public Email email() {
        return email;
    }

    public void email(Email email) {
        this.email = email;
    }
}
