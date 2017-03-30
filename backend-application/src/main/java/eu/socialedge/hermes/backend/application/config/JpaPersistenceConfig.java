package eu.socialedge.hermes.backend.application.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "eu.socialedge.hermes.backend")
@EnableJpaRepositories(basePackages = "eu.socialedge.hermes.backend")
public class JpaPersistenceConfig {
}
