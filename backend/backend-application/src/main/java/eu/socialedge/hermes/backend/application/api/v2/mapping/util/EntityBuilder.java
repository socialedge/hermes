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

    private Class<T> entityClass;
    private String idFieldName = "id";
    private Object idValue;

    public EntityBuilder(Class<T> entityClass) {
        this.entityClass = notNull(entityClass);
    }

    public static <T> EntityBuilder<T> of(Class<T> clazz) {
        return new EntityBuilder<>(clazz);
    }

    public EntityBuilder<T> idField(String name) {
        this.idFieldName = name;
        return this;
    }

    public EntityBuilder<T> idValue(Object value) {
        this.idValue = value;
        return this;
    }

    public T build() throws ReflectiveOperationException {
        val defConstructor = entityClass.getDeclaredConstructor();
        defConstructor.setAccessible(true);
        T instance = defConstructor.newInstance();

        Field idField = instance.getClass().getDeclaredField(idFieldName);
        idField.setAccessible(true);
        idField.set(instance, idValue);

        return instance;
    }
}
