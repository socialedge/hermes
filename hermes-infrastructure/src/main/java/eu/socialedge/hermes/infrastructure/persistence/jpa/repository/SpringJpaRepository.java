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
package eu.socialedge.hermes.infrastructure.persistence.jpa.repository;

import eu.socialedge.hermes.domain.Repository;
import eu.socialedge.hermes.domain.RepositoryException;
import eu.socialedge.hermes.domain.shared.Identifiable;
import eu.socialedge.hermes.domain.shared.Identifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

@NoRepositoryBean
@Transactional(readOnly = true)
public interface SpringJpaRepository<T extends Identifiable<ID>, ID extends Identifier>
        extends JpaRepository<T, ID>, Repository<T, ID> {

    @Override
    default boolean contains(ID id) {
        return get(id).isPresent();
    }

    @Override
    @Transactional
    default void add(T entity) {
        safe(() -> saveAndFlush(entity));
    }

    @Override
    @Transactional
    default void add(Collection<T> entities) {
        entities.forEach(this::add);
    }

    @Override
    @Transactional
    default void update(T entity) {
        safe(() -> saveAndFlush(entity));
    }

    @Override
    @Transactional
    default void update(Collection<T> entities) {
        entities.forEach(this::update);
    }

    @Override
    default Optional<T> get(ID id) {
        return Optional.ofNullable(safe(() -> findOne(id)));
    }

    @Override
    default Collection<T> list() {
        return safe(() -> findAll());
    }

    @Override
    @Transactional
    default boolean remove(ID id) {
        T entity = safe(() -> findOne(id));

        if (entity == null)
            return false;

        safe(() -> {
            delete(entity);
            flush();
        });

        return true;
    }

    @Override
    @Transactional
    default void remove(T entity) {
        remove(entity.id());
    }

    @Override
    @Transactional
    default void remove(Iterable<ID> entityIds) {
        entityIds.forEach(this::remove);
    }

    @Override
    @Transactional
    default void remove(Collection<T> entities) {
        entities.forEach(this::remove);
    }

    @Override
    @Transactional
    default void clear() {
        safe(() -> {
            deleteAll();
            flush();
        });
    }

    @Override
    default long size() {
        return safe(() -> count());
    }

    @Override
    default boolean isEmpty() {
        return size() == 0L;
    }

    static <E> E safe(Supplier<E> func) {
        try {
            return func.get();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    static void safe(Runnable func) {
        try {
            func.run();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }
}
