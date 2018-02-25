/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2018 SocialEdge
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

package eu.socialedge.hermes.backend.shared.domain;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class Filters implements Iterable<Filter> {

    private final List<Filter> filters = new ArrayList<>();

    public Filters(Filter... filters) {
        this(asList(filters));
    }

    public Filters(Collection<Filter> filters) {
        if (filters != null) {
            this.filters.addAll(filters);
        }
    }

    public static Filters emptyFilters() {
        return new Filters();
    }

    public Criteria asCriteria() {
        return filters.stream()
            .map(Filter::asCriteria)
            .collect(collectingAndThen(toList(),
                c -> new Criteria().andOperator(c.toArray(new Criteria[0]))));
    }

    public int size() {
        return filters.size();
    }

    public boolean isEmpty() {
        return filters.isEmpty();
    }

    public boolean contains(Filter o) {
        return filters.contains(o);
    }

    public Iterator<Filter> iterator() {
        return filters.iterator();
    }

    public boolean containsAll(Collection<Filter> c) {
        return filters.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return filters.equals(o);
    }

    @Override
    public int hashCode() {
        return filters.hashCode();
    }

    public Filter get(int index) {
        return filters.get(index);
    }

    public int indexOf(Filter o) {
        return filters.indexOf(o);
    }

    public ListIterator<Filter> listIterator() {
        return filters.listIterator();
    }

    public ListIterator<Filter> listIterator(int index) {
        return filters.listIterator(index);
    }

    public List<Filter> subList(int fromIndex, int toIndex) {
        return filters.subList(fromIndex, toIndex);
    }

    public Spliterator<Filter> spliterator() {
        return filters.spliterator();
    }

    public Stream<Filter> stream() {
        return filters.stream();
    }

    public void forEach(Consumer<? super Filter> action) {
        filters.forEach(action);
    }
}
