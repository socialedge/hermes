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

import eu.socialedge.hermes.backend.shared.domain.FilteringPagingAndSortingRepository;
import lombok.val;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

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

    public MongoFilteringPagingAndSortingRepository(MongoEntityInformation<T, ID> metadata,
                                                    MongoOperations mongoOperations) {
        super(metadata, mongoOperations);

        this.mongoOperations = mongoOperations;
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
    public Iterable<T> findAllLike(String field, String value) {
        val criterion = Criteria.where(field).regex(value);

        val query = new Query();
        query.addCriteria(criterion);

        return this.mongoOperations.find(query, entityClass);
    }
}
