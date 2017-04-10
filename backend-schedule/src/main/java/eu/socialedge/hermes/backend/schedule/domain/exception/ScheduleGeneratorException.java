package eu.socialedge.hermes.backend.schedule.domain.exception;

public class ScheduleGeneratorException extends RuntimeException {
    public ScheduleGeneratorException(String message) {
        super(message);
    }

    public ScheduleGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScheduleGeneratorException(Throwable cause) {
        super(cause);
    }
}
