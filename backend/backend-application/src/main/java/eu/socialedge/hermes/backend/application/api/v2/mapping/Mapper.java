package eu.socialedge.hermes.backend.application.api.v2.mapping;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * {@code EntityMapper} implementations maps properties from DTO to
 * domain objects and vice versa.
 *
 * @param <V> domain object type
 * @param <D> entity DTO type
 */
public interface Mapper<V, D> {

    D toDTO(V object);

    default List<D> toDTO(Iterable<V> objects) {
        if (objects == null)
            return null;

        return stream(objects.spliterator(), false).map(this::toDTO).collect(toList());
    }

    V toDomain(D dto);

    default List<V> toDomain(Iterable<D> dtos) {
        if (dtos == null)
            return null;

        return stream(dtos.spliterator(), false).map(this::toDomain).collect(toList());
    }
}
