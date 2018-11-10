package io.turbine.core.errors.exceptions.jdbc;

/**
 * This exception can occurred when we try to get the result wrapped in a SingleResult object
 * that does contain several.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class NotSingleResultException extends JdbcException {
    /**
     * The constant holding the exception defaut message
     */
    private static final String DEFAULT_MESSAGE = "Query was supposed to return a single results, but many was returned.";

    public NotSingleResultException() {
        super(DEFAULT_MESSAGE);
    }
}