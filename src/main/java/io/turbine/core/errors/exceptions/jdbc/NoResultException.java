package io.turbine.core.errors.exceptions.jdbc;

public class NoResultException extends JdbcException {
    private static final String DEFAULT_MESSAGE = "No results found for this query.";

    public NoResultException() {
        super(DEFAULT_MESSAGE);
    }
}
