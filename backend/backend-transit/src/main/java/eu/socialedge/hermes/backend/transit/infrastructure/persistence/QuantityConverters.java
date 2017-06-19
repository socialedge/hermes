package eu.socialedge.hermes.backend.transit.infrastructure.persistence;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;

public class QuantityConverters {

    @Component
    public static class QuantityToStringConverter implements Converter<Quantity<?>, String> {

        @Override
        public String convert(Quantity<?> source) {
            return source != null ? source.toString() : null;
        }
    }

    @Component
    public static class StringToQuantity implements Converter<String, Quantity<?>> {
        @Override
        public Quantity<?> convert(String source) {
            return source != null ? Quantities.getQuantity(source) : null;
        }
    }
}
