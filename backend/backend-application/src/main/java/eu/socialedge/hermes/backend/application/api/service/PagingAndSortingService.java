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

package eu.socialedge.hermes.backend.application.api.service;

import eu.socialedge.hermes.backend.application.api.mapping.Mapper;
import eu.socialedge.hermes.backend.application.api.util.Filters;
import eu.socialedge.hermes.backend.application.api.util.PageRequests;
import eu.socialedge.hermes.backend.application.api.util.Sorts;
import eu.socialedge.hermes.backend.shared.domain.Filter;
import eu.socialedge.hermes.backend.shared.domain.FilteringPagingAndSortingRepository;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.String.join;
import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * {@code PagingAndSortingService} is a base class for application services
 * that support paging and sorting
 *
 * @param <E> entity type
 * @param <I> entity's id type
 * @param <D> entity DTO
 */
@Transactional(readOnly = true)
abstract class PagingAndSortingService<E, I extends Serializable, D extends Serializable> {

    private static final String PAGE_SIZE_HEADER = "X-Page-Size";
    private static final String PAGE_NUM_HEADER = "X-Page-Number";
    private static final String PAGE_TOTAL_HEADER = "X-Page-Total";
    private static final String RESOURCE_TOTAL_HEADER = "X-Resource-Total-Records";
    private static final String ACCESS_CONTROL_EXPOSE_HEADERS_HEADER = "Access-Control-Expose-Headers";

    protected final FilteringPagingAndSortingRepository<E, I> repository;
    protected final Mapper<E, D> mapper;

    protected PagingAndSortingService(FilteringPagingAndSortingRepository<E, I> repository, Mapper<E, D> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ResponseEntity<List<D>> list(Integer size, Integer page, String sorting, String filtering, List<Predicate<D>> conditions) {
        val pageRequestOpt = PageRequests.from(size, page, sorting);
        val sortOpt = Sorts.from(sorting);
        val filterOpt = Filters.from(filtering);

        if (pageRequestOpt.isPresent()) {
            val pageRequest = pageRequestOpt.get();
            val headers = compilePageHeaders(pageRequest.getPageSize(), pageRequest.getPageNumber(), total());

            if (filterOpt.isPresent()) {
                val entities = filtered(list(pageRequest, filterOpt.get()), conditions);
                return new ResponseEntity<>(entities, headers, HttpStatus.OK);
            } else {
                val entities = filtered(list(pageRequest), conditions);
                return new ResponseEntity<>(entities, headers, HttpStatus.OK);
            }
        } else if (sortOpt.isPresent()) {
            if (filterOpt.isPresent()) {
                val entities = filtered(list(sortOpt.get(), filterOpt.get()), conditions);
                return new ResponseEntity<>(entities, HttpStatus.OK);
            } else {
                val entities = filtered(list(sortOpt.get()), conditions);
                return new ResponseEntity<>(entities, HttpStatus.OK);
            }
        } else {
            val entities = filtered(filterOpt.map(this::list).orElseGet(this::list), conditions);
            return new ResponseEntity<>(entities, HttpStatus.OK);
        }
    }

    public ResponseEntity<List<D>> list(Integer size, Integer page, String sorting, String filtering) {
        return list(size, page, sorting, filtering, emptyList());
    }

    protected List<D> list() {
        val entities = repository.findAll();
        return mapper.toDTO(entities);
    }

    protected List<D> list(Filter filter) {
        val entities = repository.findAll(filter);
        return mapper.toDTO(entities);
    }

    protected List<D> list(Sort sorting) {
        val entities = repository.findAll(sorting);
        return mapper.toDTO(entities);
    }

    protected List<D> list(Sort sorting, Filter filter) {
        val entities = repository.findAll(sorting, filter);
        return mapper.toDTO(entities);
    }

    protected List<D> list(Pageable paging) {
        val entities = repository.findAll(paging);
        return mapper.toDTO(entities);
    }

    protected List<D> list(Pageable paging, Filter filter) {
        val entities = repository.findAll(paging, filter);
        return mapper.toDTO(entities);
    }

    public ResponseEntity<D> get(I id) {
        val entity = repository.findOne(id);
        if (entity == null)
            return ResponseEntity.notFound().build();

        return new ResponseEntity<>(mapper.toDTO(entity), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<D> save(D dto) {
        val entity = mapper.toDomain(dto);
        val savedEntity = repository.save(entity);

        return new ResponseEntity<>(mapper.toDTO(savedEntity), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<D> update(I id, D dto) {
        if (!repository.exists(id))
            return ResponseEntity.notFound().build();

        val entity = mapper.toDomain(dto);
        val savedEntity = repository.save(entity);

        return new ResponseEntity<>(mapper.toDTO(savedEntity), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Void> delete(I id) {
        if (!repository.exists(id))
            return ResponseEntity.notFound().build();

        repository.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public long total() {
        return repository.count();
    }

    protected static HttpHeaders compilePageHeaders(int size, int page, long totalEntities) {
        val httpHeaders = new HttpHeaders();

        httpHeaders.add(PAGE_SIZE_HEADER, valueOf(size));
        httpHeaders.add(PAGE_NUM_HEADER, valueOf(page));
        httpHeaders.add(PAGE_TOTAL_HEADER, valueOf((int) Math.ceil((double) totalEntities / size)));
        httpHeaders.add(RESOURCE_TOTAL_HEADER, valueOf(totalEntities));
        httpHeaders.add(ACCESS_CONTROL_EXPOSE_HEADERS_HEADER, join(",", httpHeaders.keySet()));

        return httpHeaders;
    }

    private static <T> List<T> filtered(List<T> list, List<Predicate<T>> conditions) {
        Stream<T> result = list.stream();
        for (val condition : conditions) {
            result = result.filter(condition);
        }
        return result.collect(toList());
    }
}
