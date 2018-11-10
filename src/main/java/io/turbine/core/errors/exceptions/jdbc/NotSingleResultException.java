package io.turbine.core.errors.exceptions.jdbc;

public class NotSingleResultException extends JdbcException {
    private static final String DEFAULT_MESSAGE = "Query was supposed to return a single results, but many was returned.";

    public NotSingleResultException() {
        super(DEFAULT_MESSAGE);
    }
}