package io.turbine.core.errors.exceptions.http;

import java.time.Instant;

public abstract class HttpException extends RuntimeException {

    protected abstract String defaultMessage();

    public abstract int statusCode();

    private final Instant instant = Instant.now();

    protected HttpException() {
        super();

    }

    protected HttpException(String message) {
        super(message);
    }

    protected HttpException(Throwable cause) {
        super(null, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() != null ? super.getMessage() : defaultMessage();
    }

    public Instant getInstant() {
        return instant;
    }
}
