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

package eu.socialedge.hermes.backend.shared.domain;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;

/**
 * Extension of {@link PagingAndSortingRepository} to provide additional methods to retrieve
 * entities with filtering by exact and regexp field value.
 *
 * @param <T>  entity type
 * @param <ID> entity's id type
 */
@NoRepositoryBean
public interface FilteringPagingAndSortingRepository<T, ID extends Serializable>
        extends PagingAndSortingRepository<T, ID> {

    Iterable<T> findAll(String field, Object value);

    Iterable<T> findAllLike(String field, String value);
}
