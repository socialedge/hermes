package eu.socialedge.hermes.backend.application.api.v2;

import eu.socialedge.hermes.backend.application.api.LinesApi;
import eu.socialedge.hermes.backend.application.api.dto.LineDTO;
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
public class LineResource implements LinesApi {

    public ResponseEntity<List<LineDTO>> linesGet(@ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
        @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
        @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        // do some magic!
        return new ResponseEntity<List<LineDTO>>(HttpStatus.OK);
    }

    public ResponseEntity<Void> linesIdDelete(@ApiParam(value = "ID of an Line to delete",required=true ) @PathVariable("id") String id) {
        // do some magic!
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<LineDTO> linesIdGet(@ApiParam(value = "ID of a Line to fetch",required=true ) @PathVariable("id") String id) {
        // do some magic!
        return new ResponseEntity<LineDTO>(HttpStatus.OK);
    }

    public ResponseEntity<LineDTO> linesIdPut(@ApiParam(value = "ID of a Line to update",required=true ) @PathVariable("id") String id,
        @ApiParam(value = "Partial Line with new field values" ,required=true )  @Valid @RequestBody LineDTO body) {
        // do some magic!
        return new ResponseEntity<LineDTO>(HttpStatus.OK);
    }

    public ResponseEntity<LineDTO> linesPost(@ApiParam(value = "Line to add to the store" ,required=true )  @Valid @RequestBody LineDTO body) {
        // do some magic!
        return new ResponseEntity<LineDTO>(HttpStatus.OK);
    }
}
