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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

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

    /**
     * Fetches all elements that have field of a given value
     *
     * @param field filtering field name
     * @param value filtering field value
     * @return filtered elements collection by field value
     */
    Iterable<T> findAll(String field, Object value);

    /**
     * Fetches all elements where field matches given Regex pattern
     *
     * @param filter
     * @return filtered elements collection
     * @see Filter
     */
    Iterable<T> findAll(Filter filter);

    /**
     * Returns all entities sorted and filtered by the given options.
     *
     * @param sort
     * @param filter
     * @return all elements sorted and filtered by the given options
     */
    List<T> findAll(Sort sort, Filter filter);


    /**
     * Returns a {@link Page} of entities filtered by the given options and meeting
     * the paging restriction provided in the {@code Pageable} object.
     *
     * @param pageable
     * @param filter
     * @return a filtered page of elements
     */
    Page<T> findAll(Pageable pageable, Filter filter);
}
