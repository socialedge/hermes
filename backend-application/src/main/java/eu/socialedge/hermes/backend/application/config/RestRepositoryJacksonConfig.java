package eu.socialedge.hermes.backend.application.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import eu.socialedge.hermes.backend.application.serialization.QuantityDeserializer;
import eu.socialedge.hermes.backend.application.serialization.QuantitySerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import javax.measure.Quantity;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

@Configuration
public class RestRepositoryJacksonConfig extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
        objectMapper.setSerializationInclusion(NON_EMPTY);

        objectMapper.enable(INDENT_OUTPUT);

        SimpleModule quantityModule = new SimpleModule();
        quantityModule.addSerializer(new QuantitySerializer());
        quantityModule.addDeserializer(Quantity.class, new QuantityDeserializer());
        objectMapper.registerModule(quantityModule);
    }
}
