package eu.socialedge.hermes.backend.application.api.v2;

import eu.socialedge.hermes.backend.application.api.StationsApi;
import eu.socialedge.hermes.backend.application.api.dto.StationDTO;
import eu.socialedge.hermes.backend.application.api.v2.service.StationService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class StationResource implements StationsApi {

    private final StationService stationService;

    @Autowired
    public StationResource(StationService stationService) {
        this.stationService = stationService;
    }

    public ResponseEntity<List<StationDTO>> stationsGet(@ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
                                                        @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
                                                        @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        return stationService.list(size, page, sort);
    }

    public ResponseEntity<Void> stationsIdDelete(@ApiParam(value = "ID of a Station to delete",required=true ) @PathVariable("id") String id) {
        return stationService.delete(id);
    }

    public ResponseEntity<StationDTO> stationsIdGet(@ApiParam(value = "ID of a Station to fetch",required=true ) @PathVariable("id") String id) {
        return stationService.get(id);
    }

    public ResponseEntity<StationDTO> stationsIdPut(@ApiParam(value = "ID of a Station to update",required=true ) @PathVariable("id") String id,
                                                    @ApiParam(value = "Partial Station with new field values" ,required=true )  @Valid @RequestBody StationDTO body) {
        body.setId(id);
        return stationService.update(id, body);
    }

    public ResponseEntity<StationDTO> stationsPost(@ApiParam(value = "Station to add to the store" ,required=true )  @Valid @RequestBody StationDTO body) {
        return stationService.save(body);
    }
}
