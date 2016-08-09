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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static eu.socialedge.hermes.domain.shared.util.Values.requireNotNull;

public abstract class LoggingExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingExceptionMapper.class);

    protected final HttpServletRequest httpRequest;

    protected LoggingExceptionMapper(HttpServletRequest httpRequest) {
        this.httpRequest = requireNotNull(httpRequest);
    }

    public abstract ErrorMessage createResponseBody(T exception);

    @Override
    public final Response toResponse(T exception) {

        ErrorMessage responseBody = createResponseBody(exception);
        Integer responseStatus = responseBody.status();

        log(exception, responseStatus);

        return Response
                .status(responseStatus)
                .entity(responseBody)
                .build();
    }

    protected void log(T exception, Integer responseStatus) {
        if (!LOGGER.isErrorEnabled()) return;

        String msg = String.format("[%s] %s ~> %d", httpRequest.getMethod(),
                httpRequest.getRequestURL(), responseStatus);

        LOGGER.error(msg, exception);
    }
}
