/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
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

package eu.socialedge.hermes.backend.application.api.mapping;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * {@code EntityMapper} implementations maps properties from DTO to
 * domain objects and vice versa.
 *
 * @param <V> domain object type
 * @param <D> entity DTO type
 */
public interface Mapper<V, D> {

    D toDTO(V object);

    default List<D> toDTO(Iterable<V> objects) {
        if (objects == null)
            return null;

        return stream(objects.spliterator(), false).map(this::toDTO).collect(toList());
    }

    V toDomain(D dto);

    default List<V> toDomain(Iterable<D> dtos) {
        if (dtos == null)
            return null;

        return stream(dtos.spliterator(), false).map(this::toDomain).collect(toList());
    }
}
