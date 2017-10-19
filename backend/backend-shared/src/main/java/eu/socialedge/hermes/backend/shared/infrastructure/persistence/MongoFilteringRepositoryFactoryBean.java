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
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * {@link org.springframework.beans.factory.FactoryBean} to create {@link MongoFilteringRepositoryFactory} or
 * {@link MongoRepository} instances depending on repository's base interface.
 * <p>
 * This must be specified as {@code repositoryFactoryBeanClass} in {@link EnableMongoRepositories}
 * in order to enable repositories of {@link FilteringPagingAndSortingRepository} type.
 *
 * @param <R> repository type
 * @param <T> entity type
 * @param <I> entity's id type
 */
public class MongoFilteringRepositoryFactoryBean<R extends MongoRepository<T, I>, T, I extends Serializable>
        extends MongoRepositoryFactoryBean<R, T, I> {

    public MongoFilteringRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
        return new MongoFilteringRepositoryFactory<T, I>(operations);
    }

    /**
     * Factory to create {@link MongoFilteringPagingAndSortingRepository} or {@link MongoRepository} instances
     * depending on repository's base interface
     *
     * @param <T> entity type
     * @param <I> entity's id type
     */
    private static class MongoFilteringRepositoryFactory<T, I extends Serializable> extends MongoRepositoryFactory {

        private final MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext;
        private final MongoOperations mongoOperations;

        public MongoFilteringRepositoryFactory(MongoOperations mongoOperations) {
            super(mongoOperations);
            this.mongoOperations = mongoOperations;
            this.mappingContext = mongoOperations.getConverter().getMappingContext();
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            if (FilteringPagingAndSortingRepository.class.isAssignableFrom(metadata.getRepositoryInterface()))
                return MongoFilteringPagingAndSortingRepository.class;

            return super.getRepositoryBaseClass(metadata);
        }

        @Override
        protected Object getTargetRepository(RepositoryInformation metadata) {
            if (FilteringPagingAndSortingRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
                val entity = mappingContext.getPersistentEntity(metadata.getDomainType());
                val entityInformation = entityInformationFor(entity, metadata.getIdType());

                return new MongoFilteringPagingAndSortingRepository<>(entityInformation, mongoOperations);
            }

            return super.getTargetRepository(metadata);
        }

        @SuppressWarnings("unchecked")
        private MongoEntityInformation<T, I> entityInformationFor(MongoPersistentEntity<?> entity, Class<?> idType) {
            Assert.notNull(entity, "Entity must not be null!");
            return new MappingMongoEntityInformation<>((MongoPersistentEntity<T>) entity, (Class<I>) idType);
        }
    }
}
