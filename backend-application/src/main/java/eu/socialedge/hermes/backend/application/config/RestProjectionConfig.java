package eu.socialedge.hermes.backend.application.config;

import eu.socialedge.hermes.backend.application.api.projection.RichLineProjection;
import eu.socialedge.hermes.backend.application.api.projection.RichRouteProjection;
import eu.socialedge.hermes.backend.application.api.projection.RichScheduleProjection;
import eu.socialedge.hermes.backend.schedule.domain.Schedule;
import eu.socialedge.hermes.backend.transit.domain.Line;
import eu.socialedge.hermes.backend.transit.domain.Route;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

import javax.annotation.PostConstruct;

@Configuration
public class RestProjectionConfig {

    private final RepositoryRestConfiguration repositoryRestConf;

    @Autowired
    public RestProjectionConfig(RepositoryRestConfiguration repositoryRestConf) {
        this.repositoryRestConf = repositoryRestConf;
    }

    @PostConstruct
    void registerRestProjections() {
        val restDataProjections = repositoryRestConf.getProjectionConfiguration();

        restDataProjections.addProjection(RichLineProjection.class, Line.class);
        restDataProjections.addProjection(RichRouteProjection.class, Route.class);
        restDataProjections.addProjection(RichScheduleProjection.class, Schedule.class);
    }
}
