package eu.socialedge.hermes.backend.schedule.infrasturcture.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan("eu.socialedge.hermes.backend.schedule.domain")
@EnableMongoRepositories("eu.socialedge.hermes.backend.schedule.repository")
public class ScheduleConfiguration {
}
