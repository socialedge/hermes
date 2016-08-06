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
package eu.socialedge.hermes.infrastructure.persistence.jpa.repository.domain;

import eu.socialedge.hermes.domain.Repository;
import eu.socialedge.hermes.domain.RepositoryException;
import eu.socialedge.hermes.domain.shared.Identifiable;
import eu.socialedge.hermes.domain.shared.Identifier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Provides implementation of most of the basic jpa-enabled {@link Repository}
 * methods.
 *
 * @param <T> type of object the {@link Repository} impl stores
 * @param <ID> type of object's id the {@link Repository} impl stores
 * @param <JT> type of jpa-enabled object that corresponds to the object
 *             the {@link Repository} impl stores
 * @param <JID> type of jpa-enabled object's identifier that corresponds
 *              to the object's id  the {@link Repository} impl stores
 */
@Transactional(readOnly = true)
abstract class SpringRepository<T extends Identifiable<ID>, ID extends Identifier,
                                      JT, JID extends Serializable>
                                            implements Repository<T, ID> {

    protected JpaRepository<JT, JID> jpaRepository;

    protected SpringRepository(JpaRepository<JT, JID> jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean contains(ID index) {
        return findEntity(index).isPresent();
    }

    @Override
    @Transactional
    public void save(T domainObject) {
        JT jpaEntity = mapToJpaEntity(domainObject);
        saveEntity(jpaEntity);
    }

    @Override
    public Optional<T> get(ID index) {
        Optional<JT> jpaEntity = findEntity(index);

        if (!jpaEntity.isPresent())
            return Optional.empty();

        T domainObject = mapToDomainObject(jpaEntity.get());
        return Optional.of(domainObject);
    }

    @Override
    public Collection<T> list() {
        return findAllEntities().stream()
                .map(this::mapToDomainObject)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean remove(ID index) {
        Optional<JT> jpaEntityOpt = findEntity(index);

        jpaEntityOpt.ifPresent(this::deleteEntity);

        return jpaEntityOpt.isPresent();
    }

    @Override
    @Transactional
    public void remove(T entity) {
        remove(entity.id());
    }

    @Override
    @Transactional
    public void remove(Iterable<ID> entityIds) {
        entityIds.forEach(this::remove);
    }

    @Override
    @Transactional
    public void remove(Collection<T> entities) {
        entities.forEach(this::remove);
    }

    @Override
    public void clear() {
        jpaRepository.deleteAllInBatch();
    }

    @Override
    public long size() {
        return countEntities();
    }

    protected Optional<JT> findEntity(ID domainId) {
        JID entityId = mapToJpaEntityId(domainId);
        JT entity = safe(() -> jpaRepository.findOne(entityId));

        return Optional.ofNullable(entity);
    }

    protected Collection<JT> findAllEntities() {
        return safe(() -> jpaRepository.findAll());
    }

    protected void deleteEntity(JT entity) {
        safe(() -> jpaRepository.delete(entity));
    }

    protected JT saveEntity(JT entity) {
        return safe(() -> jpaRepository.save(entity));
    }

    protected long countEntities() {
        return safe(() -> jpaRepository.count());
    }

    private <E> E safe(Supplier<E> func) {
        try {
            return func.get();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    private void safe(Runnable func) {
        try {
            func.run();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    protected abstract JID mapToJpaEntityId(ID domainId);

    protected abstract T mapToDomainObject(JT jpaEntity);

    protected abstract JT mapToJpaEntity(T domainObject);
}
