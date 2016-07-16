/**
 * Hermes - The Municipal Transport Timetable System
 * Copyright (c) 2016 SocialEdge
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
package eu.socialedge.hermes.application.resource.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.LinkedHashMap;

@Provider
public class RestExceptionMapper implements ExceptionMapper<RestException> {
    @Override
    public Response toResponse(RestException ex) {
        return Response.status(ex.getStatus())
                       .entity(new LinkedHashMap<String, String>() {{
                           put("exception", ex.getClass().getCanonicalName());
                           put("message", ex.getMessage());
                           put("status", String.valueOf(ex.getStatus().getStatusCode()));
                           put("reason", ex.getStatus().getReasonPhrase());
                           if (ex.getDetails() != null) put("details", ex.getDetails());
                       }}).build();
    }
}
