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

package eu.socialedge.hermes.backend.application.api.v2.mapping.util;

import lombok.val;

import java.lang.reflect.Field;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * {@code EntityBuilder} allows to create non-JavaBean style
 * entities so they can be passing around to satisfy relations
 * without need to fetch them from repositories.
 * <p>
 * Note: EntityBuilder works only for classes with no-args
 * constructor available (can be private/default/protected too).
 *
 * @param <T> object type to initiate
 */
public final class EntityBuilder<T> {

    private static final String DEFAULT_ID_FILED_NAME = "id";

    private Class<T> entityClass;
    private String idFieldName = DEFAULT_ID_FILED_NAME;
    private Object idValue;

    @Deprecated
    public EntityBuilder(Class<T> entityClass) {
        this.entityClass = notNull(entityClass);
    }

    @Deprecated
    public static <T> EntityBuilder<T> of(Class<T> clazz) {
        return new EntityBuilder<>(clazz);
    }

    public static <T> T proxy(Class<T> clazz, String idFieldName, Object idValue)
            throws ReflectiveOperationException {
        val defConstructor = clazz.getDeclaredConstructor();
        defConstructor.setAccessible(true);
        T instance = defConstructor.newInstance();

        Field idField = instance.getClass().getDeclaredField(idFieldName);
        idField.setAccessible(true);
        idField.set(instance, idValue);

        return instance;
    }

    public static <T> T proxy(Class<T> clazz, Object idValue) throws ReflectiveOperationException {
        return proxy(clazz, DEFAULT_ID_FILED_NAME, idValue);
    }

    @Deprecated
    public EntityBuilder<T> idField(String name) {
        this.idFieldName = name;
        return this;
    }

    @Deprecated
    public EntityBuilder<T> idValue(Object value) {
        this.idValue = value;
        return this;
    }

    @Deprecated
    public T build() throws ReflectiveOperationException {
        return proxy(entityClass, idFieldName, idValue);
    }
}
