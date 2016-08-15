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
package eu.socialedge.hermes.application.service;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

public class AlreadyFoundException extends ClientErrorException {
    private static final long serialVersionUID = -6820866117511628388L;

    public AlreadyFoundException() {
        super(Response.Status.NOT_FOUND);
    }

    public AlreadyFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }

    public AlreadyFoundException(Response response) {
        super(response);
    }

    public AlreadyFoundException(String message, Response response) {
        super(message, response);
    }

    public AlreadyFoundException(Throwable cause) {
        super(Response.Status.NOT_FOUND, cause);
    }

    public AlreadyFoundException(String message, Throwable cause) {
        super(message, Response.Status.NOT_FOUND, cause);
    }

    public AlreadyFoundException(Response response, Throwable cause) {
        super(response, cause);
    }

    public AlreadyFoundException(String message, Response response, Throwable cause) {
        super(message, response, cause);
    }
}