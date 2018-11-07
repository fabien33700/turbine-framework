package io.turbine.core.errors.exceptions.jdbc;

public class NoRemainingResultsException extends JdbcException {
    private static final String DEFAULT_MESSAGE = "All results have been read from this results set.";

    public NoRemainingResultsException() {
        super(DEFAULT_MESSAGE);
    }
}
