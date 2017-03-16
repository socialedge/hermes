package eu.socialedge.hermes.backend.core;

public class TransitRuntimeException extends RuntimeException {

    public TransitRuntimeException(String message) {
        super(message);
    }

    public TransitRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransitRuntimeException(Throwable cause) {
        super(cause);
    }
}
