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
package eu.socialedge.hermes.infrastructure.persistence.jpa.entity.convert;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;

@Convert
public class LocalDateSetToStringConverter implements AttributeConverter<Set<LocalDate>, String> {
    @Override
    public String convertToDatabaseColumn(Set<LocalDate> attribute) {
        return attribute == null ? null : StringUtils.join(attribute,",");
    }

    @Override
    public Set<LocalDate> convertToEntityAttribute(String dbData) {
        if (StringUtils.isBlank(dbData))
            return new HashSet<>();

        try (Stream<String> stream = Arrays.stream(dbData.split(","))) {
            return stream.map(LocalDate::parse).collect(Collectors.toSet());
        }
    }
}
