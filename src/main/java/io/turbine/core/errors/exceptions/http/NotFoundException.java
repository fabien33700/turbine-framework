package io.turbine.core.errors.exceptions.http;

public class NotFoundException extends HttpException {

    private static final String DEFAULT_MESSAGE = "The requested element was not found.";

    public NotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    protected String defaultMessage() {
        return DEFAULT_MESSAGE;
    }

    @Override
    public final int statusCode() {
        return 404;
    }
}
