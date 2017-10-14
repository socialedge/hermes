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

package eu.socialedge.hermes.backend.application.api.v2.service;

import eu.socialedge.hermes.backend.application.api.util.PageRequests;
import eu.socialedge.hermes.backend.application.api.util.Sorts;
import eu.socialedge.hermes.backend.application.api.v2.mapping.Mapper;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

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

    protected final PagingAndSortingRepository<E, I> repository;
    protected final Mapper<E, D> mapper;

    protected PagingAndSortingService(PagingAndSortingRepository<E, I> repository, Mapper<E, D> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ResponseEntity<List<D>> list(Integer size, Integer page, String sorting) {
        val pageRequestOpt = PageRequests.from(size, page, sorting);
        val sortOpt = Sorts.from(sorting);

        if (pageRequestOpt.isPresent()) {
            val pageRequest = pageRequestOpt.get();

            val entities = list(pageRequest);
            val headers = compilePageHeaders(pageRequest.getPageSize(), pageRequest.getPageNumber(), total());

            return new ResponseEntity<>(entities, headers, HttpStatus.OK);
        } else if (sortOpt.isPresent()) {
            val entities = list(sortOpt.get());

            return new ResponseEntity<>(entities, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(list(), HttpStatus.OK);
        }
    }

    protected List<D> list() {
        val entities = repository.findAll();
        return mapper.toDTO(entities);
    }

    protected List<D> list(Sort sorting) {
        val entities = repository.findAll(sorting);
        return mapper.toDTO(entities);
    }

    protected List<D> list(Pageable paging) {
        val entities = repository.findAll(paging);
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

        httpHeaders.add(PAGE_SIZE_HEADER, String.valueOf(size));
        httpHeaders.add(PAGE_NUM_HEADER, String.valueOf(page));
        httpHeaders.add(PAGE_TOTAL_HEADER, String.valueOf((int) Math.ceil((double) totalEntities / size)));
        httpHeaders.add(RESOURCE_TOTAL_HEADER, String.valueOf(totalEntities));

        return httpHeaders;
    }
}
