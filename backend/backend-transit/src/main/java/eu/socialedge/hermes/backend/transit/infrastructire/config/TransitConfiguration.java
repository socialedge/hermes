package eu.socialedge.hermes.backend.transit.infrastructire.config;

import eu.socialedge.hermes.backend.transit.infrastructure.persistence.QuantityConverters;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static java.util.Arrays.asList;

@Configuration
@ComponentScan({
    "eu.socialedge.hermes.backend.transit.domain",
    "eu.socialedge.hermes.backend.transit.infrastructire"})
@EnableMongoRepositories("eu.socialedge.hermes.backend.transit.domain.repository")
public class TransitConfiguration {

    @Autowired
    MongoDbFactory mongoDbFactory;

    @Autowired
    MongoMappingContext mongoMappingContext;

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory, getDefaultMongoConverter());
    }

    @Bean
    public MappingMongoConverter getDefaultMongoConverter() throws Exception {
        val converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), mongoMappingContext);

        converter.setCustomConversions(customConversions());
        converter.afterPropertiesSet();

        return converter;
    }

    @Bean
    public CustomConversions customConversions() {
        return new CustomConversions(asList(
            new QuantityConverters.StringToQuantity(),
            new QuantityConverters.QuantityToStringConverter()));
    }
}
