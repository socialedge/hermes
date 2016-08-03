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
 * Represents a phone number in E.123 - an industry-standard notation
 * specified by ITU-T.
 *
 * @see <a href="https://en.wikipedia.org/wiki/E.123">wikipedia.org - E.123</a>
 */
@ValueObject
public class Phone implements Serializable {
    /**
     * Pattern used to validate ITU-T E.123 - compliant International Phone Numbers
     * @see <a href="http://goo.gl/UJ6d67">Lokesh Gupta - Java Regex : Validate International
     * Phone Numbers</a>
     */
    private static final Pattern E123_PATTERN = Pattern.compile("^\\+(?:[0-9] ?){6,14}[0-9]$");

    private final String number;

    public Phone(String number) {
        if (isBlank(number))
            throw new IllegalArgumentException("number arg cannot be black");
        else if (!E123_PATTERN.matcher(number).matches())
            throw new IllegalArgumentException("number arg must be in E.123 notation (e.g. +1 1234567890123)");

        this.number = number;
    }

    public static Phone of(String number) {
        return new Phone(number);
    }

    public String number() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Phone)) return false;
        Phone phone = (Phone) o;
        return Objects.equals(number, phone.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return number;
    }
}
