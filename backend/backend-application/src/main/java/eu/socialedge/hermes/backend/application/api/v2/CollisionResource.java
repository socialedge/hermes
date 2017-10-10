package eu.socialedge.hermes.backend.application.api.v2;

import eu.socialedge.hermes.backend.application.api.CollisionsApi;
import eu.socialedge.hermes.backend.application.api.dto.CollisionDTO;
import eu.socialedge.hermes.backend.application.api.dto.StopDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CollisionResource implements CollisionsApi {

    public ResponseEntity<List<CollisionDTO>> collisionsPost(@ApiParam(value = "Stop object to calculate collisions for" ,required=true )  @Valid @RequestBody StopDTO body) {
        // do some magic!
        return new ResponseEntity<List<CollisionDTO>>(HttpStatus.OK);
    }
}
