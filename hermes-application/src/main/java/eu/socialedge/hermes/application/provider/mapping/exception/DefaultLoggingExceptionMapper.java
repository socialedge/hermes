/**
 * Hermes - The Municipal Transport Timetable System Copyright (c) 2016 SocialEdge <p> This program
 * is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. <p> This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */
package eu.socialedge.hermes.application.provider.mapping.exception;

import eu.socialedge.hermes.application.provider.mapping.exception.message.ErrorMessage;

import javax.servlet.http.HttpServletRequest;

import static javax.ws.rs.core.Response.Status;

public abstract class DefaultLoggingExceptionMapper<T extends Throwable>
                    extends LoggingExceptionMapper<T> {

    private static final Integer DEFAULT_RESPONSE_CODE =
            Status.INTERNAL_SERVER_ERROR.getStatusCode();

    protected DefaultLoggingExceptionMapper(HttpServletRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public ErrorMessage createResponseBody(T exception) {
        return new ErrorMessage(httpRequest.getRequestURI(),
                exception.getClass().getSimpleName() + ": " + exception.getMessage(),
                DEFAULT_RESPONSE_CODE);
    }
}
