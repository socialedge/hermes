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

import eu.socialedge.hermes.domain.shared.Identifiable;
import eu.socialedge.hermes.domain.shared.Identifier;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface for generic operations on a repository for a specific type.
 *
 * @param <T> type of object this repository stores
 * @param <ID> type of object's identifier this repository stores
 */
public interface Repository<T extends Identifiable<ID>, ID extends Identifier> {

    boolean contains(ID id);

    void save(T object);

    Optional<T> get(ID id);

    Collection<T> list();

    boolean remove(ID id);

    void remove(T object);

    void remove(Iterable<ID> entityIds);

    void remove(Collection<T> entities);

    void clear();

    long size();
}
