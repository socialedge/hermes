/**
 * Hermes - The Municipal Transport Timetable System Copyright (c) 2016 SocialEdge <p> This program
 * is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. <p> This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 */
package eu.socialedge.hermes.application.provider.mapping.exception.message;

import java.net.URI;
import java.net.URL;
import java.util.Collection;

import static eu.socialedge.hermes.util.Iterables.requireNotEmpty;
import static eu.socialedge.hermes.util.Values.requireNotNull;

public class ValidationExceptionSpecification extends ExceptionSpecification {

    private final Collection<ValidationError> errors;

    public ValidationExceptionSpecification(URI source, String title, Integer httpStatus,
                                            Collection<ValidationError> errors, String detail, URL help) {
        super(source, title, httpStatus, detail, help);
        this.errors = requireNotEmpty(errors);
    }

    public ValidationExceptionSpecification(String source, String title, Integer httpStatus,
                                            Collection<ValidationError> errors, String detail, String help) {
        super(source, title, httpStatus, detail, help);
        this.errors = requireNotEmpty(errors);
    }

    public ValidationExceptionSpecification(URI source, String title, Integer httpStatus,
                                            Collection<ValidationError> errors) {
        this(source, title, httpStatus, errors, null, null);
    }

    public ValidationExceptionSpecification(String source, String title, Integer httpStatus,
                                            Collection<ValidationError> errors) {
        this(source, title, httpStatus, errors, null, null);
    }

    public boolean addValidationError(ValidationError error) {
        return errors.add(error);
    }

    public boolean removeValidationError(ValidationError error) {
        return errors.remove(error);
    }

    public static class ValidationError {
        private final String field;
        private final Object rejected;
        private final String message;

        public ValidationError(String field, Object rejected, String message) {
            this.field = requireNotNull(field);
            this.rejected = rejected;
            this.message = message;
        }

        public ValidationError(String message) {
            this.message = message;
            this.rejected = null;
            this.field = null;
        }
    }
}
