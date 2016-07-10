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
package eu.socialedge.hermes.server.resource.exception;

import javax.ws.rs.core.Response;

public class BadRequestException extends RestException {
    private static final Response.Status STATUS = Response.Status.BAD_REQUEST;

    public BadRequestException(String message) {
        super(STATUS, message);
    }

    public BadRequestException(String message, String details) {
        super(STATUS, message, details);
    }

    public BadRequestException(String message, Throwable cause) {
        super(STATUS, message, cause);
    }

    public BadRequestException(String message, String details, Throwable cause) {
        super(STATUS, message, details, cause);
    }

    public BadRequestException(Throwable cause) {
        super(STATUS, cause);
    }
}
