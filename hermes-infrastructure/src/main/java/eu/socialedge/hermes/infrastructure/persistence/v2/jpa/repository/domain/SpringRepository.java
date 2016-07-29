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
package eu.socialedge.hermes.infrastructure.persistence.v2.jpa.repository.domain;

import eu.socialedge.hermes.domain.v2.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
abstract class SpringRepository<T, ID extends Serializable,
                                      JT, JID extends Serializable>
                                            implements Repository<T, ID> {

    protected JpaRepository<JT, JID> jpaRepository;

    protected SpringRepository(JpaRepository<JT, JID> jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean contains(ID index) {
        return findOne(index).isPresent();
    }

    @Override
    @Transactional
    public void store(T domainObject) {
        JT jpaEntity = mapToJpaEntity(domainObject);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public Optional<T> get(ID index) {
        Optional<JT> jpaEntity = findOne(index);

        if (!jpaEntity.isPresent())
            return Optional.empty();

        T domainObject = mapToDomainObject(jpaEntity.get());
        return Optional.of(domainObject);
    }

    @Override
    public Collection<T> list() {
        return jpaRepository.findAll().stream()
                .map(this::mapToDomainObject).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean remove(ID index) {
        Optional<JT> jpaEntityOpt = findOne(index);
        if (!jpaEntityOpt.isPresent())
            return false;

        jpaRepository.delete(jpaEntityOpt.get());
        return true;
    }

    @Override
    @Transactional
    public boolean remove(T entity) {
        ID domainId = extractDomainId(entity);
        return remove(domainId);
    }

    @Override
    @Transactional
    public int remove(Iterable<ID> entityIds) {
        int removed = 0;

        for(ID entityId : entityIds) {
            if (remove(entityId))
                removed++;
        }

        return removed;
    }

    @Override
    @Transactional
    public int remove(Collection<T> entities) {
        int removed = 0;

        for(T entity : entities) {
            if (remove(entity))
                removed++;
        }

        return removed;
    }

    @Override
    public long size() {
        return jpaRepository.count();
    }

    protected Optional<JT> findOne(ID domainId) {
        return Optional.ofNullable(jpaRepository.findOne(mapToJpaEntityId(domainId)));
    }

    protected abstract ID extractDomainId(T domainObject);

    protected abstract JID mapToJpaEntityId(ID domainId);

    protected abstract T mapToDomainObject(JT jpaEntity);

    protected abstract JT mapToJpaEntity(T domainObject);
}
