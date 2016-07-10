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

public class RestException extends RuntimeException {
    private Response.Status status;
    private String details;

    public RestException(Response.Status status, String message) {
        super(message);
        this.status = status;
    }

    public RestException(Response.Status status, String message, String details) {
        super(message);
        this.status = status;
        this.details = details;
    }

    public RestException(Response.Status status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public RestException(Response.Status status, String message, String details, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.details = details;
    }

    public RestException(Response.Status status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }
}
