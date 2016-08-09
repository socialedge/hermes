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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper extends LoggingExceptionMapper<WebApplicationException> {

    protected WebApplicationExceptionMapper(@Context HttpServletRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public ErrorMessage createResponseBody(WebApplicationException ex) {
        return new ErrorMessage(httpRequest.getRequestURI(),
                               ex.getMessage(),
                               ex.getResponse().getStatus());
    }
}
