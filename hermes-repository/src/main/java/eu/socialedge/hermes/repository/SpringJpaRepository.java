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
package eu.socialedge.hermes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public abstract class SpringJpaRepository<ID extends Serializable, T> implements Repository<ID, T> {
    private JpaRepository<T, ID> internalRepository;

    public SpringJpaRepository(JpaRepository<T, ID> internalRepository) {
        this.internalRepository = internalRepository;
    }

    @Override
    public boolean contains(ID index) {
        try {
            return internalRepository.exists(index);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean add(T entity) {
        try {
            internalRepository.save(entity);
            return true;
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Optional<T> get(ID index) {
        try {
            return Optional.ofNullable(internalRepository.findOne(index));
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Collection<T> list() {
        try {
            return internalRepository.findAll();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void remove(ID index) {
        try {
            internalRepository.delete(index);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void remove(T entity) {
        try {
            internalRepository.delete(entity);
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public long size() {
        try {
            return internalRepository.count();
        } catch (Exception e) {
            throw new RepositoryException(e);
        }
    }
}
