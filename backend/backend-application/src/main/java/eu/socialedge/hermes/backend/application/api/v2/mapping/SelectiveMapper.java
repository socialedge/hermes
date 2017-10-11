package eu.socialedge.hermes.backend.application.api.v2.mapping;

/**
 * {@code SelectiveMapper} implementations extends {@link Mapper}
 * with a mapping method for selective update mapping dto props
 * to domain object
 *
 * @param <V> domain object type
 * @param <D> entity DTO type
 */
public interface SelectiveMapper<V, D> extends Mapper<V, D> {

    void update(V object, D dto);
}
