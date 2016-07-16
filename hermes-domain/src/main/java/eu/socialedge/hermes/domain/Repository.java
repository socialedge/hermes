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
package eu.socialedge.hermes.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface Repository<ID extends Serializable, T> {

    boolean contains(ID index);

    T store(T entity);

    Optional<T> get(ID index);

    Collection<T> list();

    void remove(ID index);

    void remove(T entity);

    void remove(Collection<T> entities);

    long size();
}