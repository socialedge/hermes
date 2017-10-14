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
