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

    @Override
    public ResponseEntity<List<CollisionDTO>> collisionsPost(@ApiParam(value = "Stop object to calculate collisions for", required = true) @Valid @RequestBody StopDTO body) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
