package eu.socialedge.hermes.backend.application.api.v2;

import eu.socialedge.hermes.backend.application.api.StationsApi;
import eu.socialedge.hermes.backend.application.api.dto.StationDTO;
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
public class StationResource implements StationsApi {

    public ResponseEntity<List<StationDTO>> stationsGet(@ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
        @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
        @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        // do some magic!
        return new ResponseEntity<List<StationDTO>>(HttpStatus.OK);
    }

    public ResponseEntity<Void> stationsIdDelete(@ApiParam(value = "ID of a Station to delete",required=true ) @PathVariable("id") String id) {
        // do some magic!
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<StationDTO> stationsIdGet(@ApiParam(value = "ID of a Station to fetch",required=true ) @PathVariable("id") String id) {
        // do some magic!
        return new ResponseEntity<StationDTO>(HttpStatus.OK);
    }

    public ResponseEntity<StationDTO> stationsIdPut(@ApiParam(value = "ID of a Station to update",required=true ) @PathVariable("id") String id,
        @ApiParam(value = "Partial Station with new field values" ,required=true )  @Valid @RequestBody StationDTO body) {
        // do some magic!
        return new ResponseEntity<StationDTO>(HttpStatus.OK);
    }

    public ResponseEntity<StationDTO> stationsPost(@ApiParam(value = "Station to add to the store" ,required=true )  @Valid @RequestBody StationDTO body) {
        // do some magic!
        return new ResponseEntity<StationDTO>(HttpStatus.OK);
    }
}
