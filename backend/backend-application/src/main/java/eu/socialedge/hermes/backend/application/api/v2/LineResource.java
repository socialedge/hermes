/*
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016-2017 SocialEdge
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package eu.socialedge.hermes.backend.application.api.v2;

import eu.socialedge.hermes.backend.application.api.LinesApi;
import eu.socialedge.hermes.backend.application.api.dto.LineDTO;
import eu.socialedge.hermes.backend.application.api.v2.service.LineService;
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
public class LineResource implements LinesApi {

    private final LineService lineService;

    @Autowired
    public LineResource(LineService lineService) {
        this.lineService = lineService;
    }

    public ResponseEntity<List<LineDTO>> linesGet(@ApiParam(value = "Limits an amount of entities per page") @RequestParam(value = "size", required = false) Integer size,
                                                  @ApiParam(value = "Number of list page to display") @RequestParam(value = "page", required = false) Integer page,
                                                  @ApiParam(value = "Defines a sort params for the query e.g ?sort=name,ASC") @RequestParam(value = "sort", required = false) String sort) {
        return lineService.list(size, page, sort);
    }

    public ResponseEntity<Void> linesIdDelete(@ApiParam(value = "ID of an Line to delete", required = true) @PathVariable("id") String id) {
        return lineService.delete(id);
    }

    public ResponseEntity<LineDTO> linesIdGet(@ApiParam(value = "ID of a Line to fetch", required = true) @PathVariable("id") String id) {
        return lineService.get(id);
    }

    public ResponseEntity<LineDTO> linesIdPut(@ApiParam(value = "ID of a Line to update", required = true) @PathVariable("id") String id,
                                              @ApiParam(value = "Partial Line with new field values", required = true) @Valid @RequestBody LineDTO body) {
        body.setId(id);
        return lineService.update(id, body);
    }

    public ResponseEntity<LineDTO> linesPost(@ApiParam(value = "Line to add to the store", required = true) @Valid @RequestBody LineDTO body) {
        return lineService.save(body);
    }
}
