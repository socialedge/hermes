package eu.socialedge.hermes.backend.application.api.v2;

import eu.socialedge.hermes.backend.application.api.AgenciesApi;
import eu.socialedge.hermes.backend.application.api.dto.AgencyDTO;
import eu.socialedge.hermes.backend.application.api.v2.service.AgencyService;
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
public class AgencyResource implements AgenciesApi {

    private final AgencyService agencyService;

    @Autowired
    public AgencyResource(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @Override
    public ResponseEntity<List<AgencyDTO>> agenciesGet(@ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
                                                       @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
                                                       @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        return agencyService.list(size, page, sort);
    }

    @Override
    public ResponseEntity<Void> agenciesIdDelete(@ApiParam(value = "ID of an Agency to delete",required=true ) @PathVariable("id") String id) {
        return agencyService.delete(id);
    }

    @Override
    public ResponseEntity<AgencyDTO> agenciesIdGet(@ApiParam(value = "ID of an Agency to fetch",required=true ) @PathVariable("id") String id) {
        return agencyService.get(id);
    }

    @Override
    public ResponseEntity<AgencyDTO> agenciesIdPut(@ApiParam(value = "ID of an Agency to update",required=true ) @PathVariable("id") String id,
                                                   @ApiParam(value = "Partial Agency with new field values" ,required=true )  @Valid @RequestBody AgencyDTO body) {
        body.setId(id);
        return agencyService.update(id, body);
    }

    @Override
    public ResponseEntity<AgencyDTO> agenciesPost(@ApiParam(value = "Agency to add to the store" ,required=true )  @Valid @RequestBody AgencyDTO body) {
        return agencyService.save(body);
    }
}
