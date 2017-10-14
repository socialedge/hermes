package eu.socialedge.hermes.backend.application.api.v2;

import eu.socialedge.hermes.backend.application.api.SchedulesApi;
import eu.socialedge.hermes.backend.application.api.dto.CollisionDTO;
import eu.socialedge.hermes.backend.application.api.dto.ScheduleDTO;
import eu.socialedge.hermes.backend.application.api.dto.TripDTO;
import eu.socialedge.hermes.backend.application.api.v2.service.ScheduleService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleResource(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public ResponseEntity<List<ScheduleDTO>> schedulesGet(@ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
                                                          @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
                                                          @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        return scheduleService.list(size, page, sort);
    }

    @Override
    public ResponseEntity<List<CollisionDTO>> schedulesIdCollisionsGet(@ApiParam(value = "ID of a Schedule", required = true) @PathVariable("id") String id,
                                                                       @ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
                                                                       @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
                                                                       @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public ResponseEntity<Void> schedulesIdDelete(@ApiParam(value = "ID of a schedule to delete", required = true) @PathVariable("id") String id) {
        return scheduleService.delete(id);
    }

    @Override
    public ResponseEntity<ScheduleDTO> schedulesIdGet(@ApiParam(value = "ID of a Schedule to fetch", required = true) @PathVariable("id") String id) {
        return scheduleService.get(id);
    }

    @Override
    public ResponseEntity<List<TripDTO>> schedulesIdInboundTripsGet(@ApiParam(value = "ID of a Schedule", required = true) @PathVariable("id") String id) {

        return scheduleService.inboundTrips(id);
    }

    @Override
    public ResponseEntity<List<TripDTO>> schedulesIdOutboundTripsGet(@ApiParam(value = "ID of a Schedule", required = true) @PathVariable("id") String id) {
        return scheduleService.outboundTrips(id);
    }

    @Override
    public ResponseEntity<ScheduleDTO> schedulesIdPut(@ApiParam(value = "ID of a Schedule to update", required = true) @PathVariable("id") String id,
                                                      @ApiParam(value = "Partial Schedule with new field values", required = true) @Valid @RequestBody ScheduleDTO body) {
        body.setId(id);
        return scheduleService.update(id, body);
    }

    @Override
    public ResponseEntity<ScheduleDTO> schedulesPost(@ApiParam(value = "Schedule to add to the store", required = true) @Valid @RequestBody ScheduleDTO body) {
        return scheduleService.save(body);
    }
}
