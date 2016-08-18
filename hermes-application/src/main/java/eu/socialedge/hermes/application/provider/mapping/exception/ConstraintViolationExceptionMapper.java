/**
 * Hermes - The Municipal Transport Timetable System Copyright (c) 2016 SocialEdge <p> This program
 * is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. <p> This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */
package eu.socialedge.hermes.application.provider.mapping.exception;

import eu.socialedge.hermes.application.provider.mapping.exception.message.ExceptionSpecification;
import eu.socialedge.hermes.application.provider.mapping.exception.message.ValidationExceptionSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import static eu.socialedge.hermes.application.provider.mapping.exception.message.ValidationExceptionSpecification.ValidationError;
import static eu.socialedge.hermes.util.Strings.isBlank;
import static java.util.Objects.isNull;
import static javax.ws.rs.core.Response.Status;

@Provider
public class ConstraintViolationExceptionMapper
        extends LoggingExceptionMapper<ConstraintViolationException> {

    private static final Integer CONSTRAINT_VIOLATION_RESPONSE_CODE =
            Status.BAD_REQUEST.getStatusCode();

    protected ConstraintViolationExceptionMapper(@Context HttpServletRequest httpRequest) {
        super(httpRequest);
    }

    @Override
    public ExceptionSpecification createResponseBody(ConstraintViolationException exception) {
        Collection<ValidationError> validationErrors = new ArrayList<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            Path.Node pathNode = findLastNonEmptyPathNode(violation.getPropertyPath());

            if (!isNull(pathNode) && pathNode.getKind() == ElementKind.PROPERTY) {
                validationErrors.add(createValidationError(violation, pathNode));
            } else {
                validationErrors.add(createValidationError(violation));
            }
        }

        return new ValidationExceptionSpecification(httpRequest.getRequestURI(), exception.getMessage(),
                CONSTRAINT_VIOLATION_RESPONSE_CODE, validationErrors);
    }

    private Path.Node findLastNonEmptyPathNode(Path path) {
        return StreamSupport.stream(path.spliterator(), false)
                    .filter(node -> !isBlank(node.getName()))
                    .findFirst().orElse(null);
    }

    private ValidationError createValidationError(ConstraintViolation<?> error, Path.Node node) {
        return new ValidationError(node.getName(), error.getInvalidValue(), error.getMessage());
    }

    private ValidationError createValidationError(ConstraintViolation<?> error) {
        return new ValidationError(error.getMessage());
    }
}
