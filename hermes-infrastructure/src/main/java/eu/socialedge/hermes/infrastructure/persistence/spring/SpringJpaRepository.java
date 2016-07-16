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
package eu.socialedge.hermes.infrastructure.persistence.spring;

import eu.socialedge.hermes.domain.Repository;
import eu.socialedge.hermes.domain.RepositoryException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

@NoRepositoryBean
public interface SpringJpaRepository<ID extends Serializable, T> extends Repository<ID, T>, JpaRepository<T, ID> {
    @Override
    default boolean contains(ID index) {
        try {
            return exists(index);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    default T store(T entity) {
        try {
            return save(entity);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    default Optional<T> get(ID index) {
        try {
            T entity = findOne(index);
            if (entity == null)
                return Optional.empty();

            return Optional.of(save(entity));
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    default Collection<T> list() {
        try {
            return findAll();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    default void remove(ID index) {
        try {
            delete(index);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    default void remove(T entity) {
        try {
            delete(entity);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    default void remove(Collection<T> entities) {
        try {
            delete(entities);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    default long size() {
        try {
            return count();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }
}
