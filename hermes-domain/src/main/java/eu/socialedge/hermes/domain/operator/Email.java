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

import eu.socialedge.hermes.domain.ext.ValueObject;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Represents an RFC5322 - compliant email address.
 */
@ValueObject
public class Email implements Serializable {
    /**
     * Pattern used to validate EMAIL address. RFC 5322 ++
     * @see <a href="http://goo.gl/D3sgOC">Lokesh Gupta - Java Regex : Validate Email Address</a>
     */
    private static final Pattern RFC5322P_PATTERN = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+" +
            "(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    private static final String AT_SIGN = "@";

    private final String email;

    private final String localPart;
    private final String domainPart;

    public Email(String email) {
        if (isBlank(email))
            throw new IllegalArgumentException("email arg cannot be black");
        else if (!RFC5322P_PATTERN.matcher(email).matches())
            throw new IllegalArgumentException("number arg is not a valid email address");

        this.email = email;

        this.localPart = extractLocalPart(email);
        this.domainPart = extractDomainPart(email);
    }

    public static Email of(String email) {
        return new Email(email);
    }

    public String address() {
        return email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email1 = (Email) o;
        return Objects.equals(email, email1.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return email;
    }
}
