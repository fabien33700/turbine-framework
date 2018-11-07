package io.turbine.core.errors.exceptions.jdbc;

public class TransformationException extends JdbcException {
    private static final String DEFAULT_MESSAGE = "Could not transform raw results to a model instance.";

    public TransformationException() {
        super(DEFAULT_MESSAGE);
    }

    public TransformationException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
