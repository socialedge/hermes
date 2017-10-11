package eu.socialedge.hermes.backend.application.api.v2;

import eu.socialedge.hermes.backend.application.api.SchedulesApi;
import eu.socialedge.hermes.backend.application.api.dto.CollisionDTO;
import eu.socialedge.hermes.backend.application.api.dto.ScheduleDTO;
import eu.socialedge.hermes.backend.application.api.dto.TripDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ScheduleResource implements SchedulesApi {

    public ResponseEntity<List<ScheduleDTO>> schedulesGet(@ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
        @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
        @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        // do some magic!
        return new ResponseEntity<List<ScheduleDTO>>(HttpStatus.OK);
    }

    public ResponseEntity<List<CollisionDTO>> schedulesIdCollisionsGet(@ApiParam(value = "ID of a Schedule",required=true ) @PathVariable("id") String id,
        @ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
        @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
        @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        // do some magic!
        return new ResponseEntity<List<CollisionDTO>>(HttpStatus.OK);
    }

    public ResponseEntity<Void> schedulesIdDelete(@ApiParam(value = "ID of a schedule to delete",required=true ) @PathVariable("id") String id) {
        // do some magic!
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<ScheduleDTO> schedulesIdGet(@ApiParam(value = "ID of a Schedule to fetch",required=true ) @PathVariable("id") String id) {
        // do some magic!
        return new ResponseEntity<ScheduleDTO>(HttpStatus.OK);
    }

    public ResponseEntity<List<TripDTO>> schedulesIdInboundTripsGet(@ApiParam(value = "ID of a Schedule",required=true ) @PathVariable("id") String id,
        @ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
        @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
        @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        // do some magic!
        return new ResponseEntity<List<TripDTO>>(HttpStatus.OK);
    }

    public ResponseEntity<List<TripDTO>> schedulesIdOutboundTripsGet(@ApiParam(value = "ID of a Schedule",required=true ) @PathVariable("id") String id,
        @ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
        @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
        @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        // do some magic!
        return new ResponseEntity<List<TripDTO>>(HttpStatus.OK);
    }

    public ResponseEntity<ScheduleDTO> schedulesIdPut(@ApiParam(value = "ID of a Schedule to update",required=true ) @PathVariable("id") String id,
        @ApiParam(value = "Partial Schedule with new field values" ,required=true )  @Valid @RequestBody ScheduleDTO body) {
        // do some magic!
        return new ResponseEntity<ScheduleDTO>(HttpStatus.OK);
    }

    public ResponseEntity<ScheduleDTO> schedulesPost(@ApiParam(value = "Schedule to add to the store" ,required=true )  @Valid @RequestBody ScheduleDTO body) {
        // do some magic!
        return new ResponseEntity<ScheduleDTO>(HttpStatus.OK);
    }
}
