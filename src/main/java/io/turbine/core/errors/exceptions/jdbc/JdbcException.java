package io.turbine.core.errors.exceptions.jdbc;

/**
 * The base exception for representing a JDBC error.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public abstract class JdbcException extends RuntimeException {

    protected JdbcException(String message) {
        super(message);
    }

    public JdbcException(String message, Throwable cause) {
        super(message, cause);
    }

    public JdbcException(Throwable cause) {
        super(cause);
    }

}
