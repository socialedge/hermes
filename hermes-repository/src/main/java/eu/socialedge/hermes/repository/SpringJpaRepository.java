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

public abstract class SpringJpaRepository<ID extends Serializable, T> implements Repository<ID, T> {
    private JpaRepository<T, ID> internalRepository;

    public SpringJpaRepository(JpaRepository<T, ID> internalRepository) {
        this.internalRepository = internalRepository;
    }

    @Override
    public boolean contains(ID index) {
        return internalRepository.exists(index);
    }

    @Override
    public boolean add(T entity) {
        internalRepository.save(entity);
        return true;
    }

    @Override
    public T get(ID index) {
        return internalRepository.getOne(index);
    }

    @Override
    public void remove(ID index) {
        internalRepository.delete(index);
    }

    @Override
    public void remove(T entity) {
        internalRepository.delete(entity);
    }

    @Override
    public long size() {
        return internalRepository.count();
    }
}
