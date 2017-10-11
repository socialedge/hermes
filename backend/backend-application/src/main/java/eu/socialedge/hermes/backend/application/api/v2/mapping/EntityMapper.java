package eu.socialedge.hermes.backend.application.api.v2.mapping;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * {@code EntityMapper} implementations maps properties from DTO to
 * entities and vice versa.
 *
 * @param <E> entity type
 * @param <D> entity DTO type
 */
public interface EntityMapper<E, D> {

    D toDTO(E entity);

    default List<D> toDTO(Iterable<E> entities) {
        return stream(entities.spliterator(), false).map(this::toDTO).collect(toList());
    }

    E toEntity(D dto);

    default List<E> toEntity(Iterable<D> dtos) {
        return stream(dtos.spliterator(), false).map(this::toEntity).collect(toList());
    }

    void updateEntity(E entity, D dto);
}
