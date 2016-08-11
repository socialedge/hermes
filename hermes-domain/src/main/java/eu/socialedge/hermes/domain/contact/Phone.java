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
 * Represents a phone number in E.123 - an industry-standard notation
 * specified by ITU-T.
 *
 * @see <a href="https://en.wikipedia.org/wiki/E.123">wikipedia.org - E.123</a>
 */
@ValueObject
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@EqualsAndHashCode @ToString
@Embeddable
public class Phone implements Serializable {
    /**
     * Pattern used to validate ITU-T E.123 - compliant International Phone Numbers
     * @see <a href="http://goo.gl/UJ6d67">Lokesh Gupta - Java Regex : Validate International
     * Phone Numbers</a>
     */
    private static final Pattern E123_PATTERN = Pattern.compile("^\\+(?:[0-9] ?){6,14}[0-9]$");

    @Column(name = "phone_number")
    private final String number;

    public Phone(String number) {
        if (!E123_PATTERN.matcher(requireNotBlank(number)).matches())
            throw new IllegalArgumentException("number arg must be in E.123 notation (e.g. +1 1234567890123)");

        this.number = number;
    }

    public static Phone of(String number) {
        return new Phone(number);
    }

    public String number() {
        return number;
    }
}
