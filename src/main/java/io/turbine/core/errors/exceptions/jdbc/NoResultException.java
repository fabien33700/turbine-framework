package io.turbine.core.errors.exceptions.jdbc;

/**
 * This exception can occurred when we try to get the result from a Results implementation
 * that does not contain any.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class NoResultException extends JdbcException {
    /**
     * The constant holding the exception defaut message
     */
    private static final String DEFAULT_MESSAGE = "No results found for this query.";

    public NoResultException() {
        super(DEFAULT_MESSAGE);
    }
}
