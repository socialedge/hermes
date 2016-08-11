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
package eu.socialedge.hermes.domain.contact;

import eu.socialedge.hermes.domain.ext.ValueObject;

import java.io.Serializable;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static eu.socialedge.hermes.util.Strings.requireNotBlank;

/**
 * Represents an RFC5322 - compliant address address.
 */
@ValueObject
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode @ToString
@Embeddable
public class Email implements Serializable {
    /**
     * Pattern used to validate EMAIL address. RFC 5322 ++
     * @see <a href="http://goo.gl/D3sgOC">Lokesh Gupta - Java Regex : Validate Email Address</a>
     */
    private static final Pattern RFC5322P_PATTERN = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+" +
            "(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    private static final String AT_SIGN = "@";

    @Column(name = "email_address")
    private final String address;

    private transient final String localPart;
    private transient final String domainPart;

    public Email(String address) {
        if (!RFC5322P_PATTERN.matcher(requireNotBlank(address)).matches())
            throw new IllegalArgumentException("number arg is not a valid address address");

        this.address = address;

        this.localPart = extractLocalPart(address);
        this.domainPart = extractDomainPart(address);
    }

    public static Email of(String email) {
        return new Email(email);
    }

    public String address() {
        return address;
    }

    public String mailboxName() {
        return localPart;
    }

    public String domainName() {
        return domainPart;
    }

    private String extractLocalPart(String email) {
        return email.split(AT_SIGN)[0];
    }

    private String extractDomainPart(String email) {
        return email.split(AT_SIGN)[1];
    }
}
