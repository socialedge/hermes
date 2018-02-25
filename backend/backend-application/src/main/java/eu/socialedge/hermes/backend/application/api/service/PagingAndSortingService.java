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
import eu.socialedge.hermes.backend.application.api.util.FiltersParser;
import eu.socialedge.hermes.backend.application.api.util.PageRequestsParser;
import eu.socialedge.hermes.backend.application.api.util.SortsParser;
import eu.socialedge.hermes.backend.shared.domain.Filter;
import eu.socialedge.hermes.backend.shared.domain.FilteringPagingAndSortingRepository;
import eu.socialedge.hermes.backend.shared.domain.Filters;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

import static java.lang.String.join;
import static java.lang.String.valueOf;

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

    public ResponseEntity<List<D>> list(Integer size, Integer page, String sorting, String filtering) {
        val pageRequestOpt = PageRequestsParser.from(size, page, sorting);
        val sortOpt = SortsParser.from(sorting);
        val filters = FiltersParser.fromMultiple(filtering);

        if (pageRequestOpt.isPresent()) {
            val pageRequest = pageRequestOpt.get();
            val headers = compilePageHeaders(pageRequest.getPageSize(), pageRequest.getPageNumber(), total());

            if (!filters.isEmpty()) {
                val entities = list(pageRequest, filters);
                return new ResponseEntity<>(entities, headers, HttpStatus.OK);
            } else {
                val entities = list(pageRequest);
                return new ResponseEntity<>(entities, headers, HttpStatus.OK);
            }
        } else if (sortOpt.isPresent()) {
            if (!filters.isEmpty()) {
                val entities = list(sortOpt.get(), filters);
                return new ResponseEntity<>(entities, HttpStatus.OK);
            } else {
                val entities = list(sortOpt.get());
                return new ResponseEntity<>(entities, HttpStatus.OK);
            }
        } else {
            if (!filters.isEmpty()) {
                return new ResponseEntity<>(list(filters), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(list(), HttpStatus.OK);
            }
        }
    }

    protected List<D> list() {
        val entities = repository.findAll();
        return mapper.toDTO(entities);
    }

    protected List<D> list(Filter filter) {
        val entities = repository.findAll(filter);
        return mapper.toDTO(entities);
    }

    protected List<D> list(Filters filters) {
        val entities = repository.findAll(filters);
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

    protected List<D> list(Sort sorting, Filters filters) {
        val entities = repository.findAll(sorting, filters);
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

    protected List<D> list(Pageable paging, Filters filters) {
        val entities = repository.findAll(paging, filters);
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
}
