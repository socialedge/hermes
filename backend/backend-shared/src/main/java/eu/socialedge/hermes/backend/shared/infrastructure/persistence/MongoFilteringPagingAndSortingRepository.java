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

package eu.socialedge.hermes.backend.shared.infrastructure.persistence;

import eu.socialedge.hermes.backend.shared.domain.Filter;
import eu.socialedge.hermes.backend.shared.domain.FilteringPagingAndSortingRepository;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * {@code MongoFilteringPagingAndSortingRepository} is an extension of the base
 * mongo repository implementation for Mongo that provides an implementation for
 * filtering methods of {@link FilteringPagingAndSortingRepository}.
 *
 * @param <T>  entity type
 * @param <ID> entity's id type
 */
public class MongoFilteringPagingAndSortingRepository<T, ID extends Serializable>
        extends SimpleMongoRepository<T, ID>
        implements FilteringPagingAndSortingRepository<T, ID> {

    private final MongoOperations mongoOperations;

    private final Class<T> entityClass;
    private final String entityCollectionName;

    public MongoFilteringPagingAndSortingRepository(MongoEntityInformation<T, ID> metadata,
                                                    MongoOperations mongoOperations) {
        super(metadata, mongoOperations);

        this.mongoOperations = mongoOperations;

        this.entityCollectionName = metadata.getCollectionName();
        this.entityClass = metadata.getJavaType();
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<T> findAll(String field, Object value) {
        val criterion = Criteria.where(field).is(value);

        val query = new Query();
        query.addCriteria(criterion);

        return this.mongoOperations.find(query, entityClass);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<T> findAll(Filter filter) {
        return findAll(new Query(filter.asCriteria()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll(Sort sort, Filter filter) {
        val likeCriterion = Criteria.where(filter.field()).regex(filter.regexp());
        return findAll(new Query(likeCriterion).with(sort));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable, Filter filter) {
        val count = count();
        val pageableResultList = findAll(new Query(filter.asCriteria()).with(pageable));
        return new PageImpl<>(pageableResultList, pageable, count);
    }

    private List<T> findAll(Query query) {
        if (query == null)
            return Collections.emptyList();

        return mongoOperations.find(query, entityClass, entityCollectionName);
    }
}
