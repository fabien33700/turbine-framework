package io.turbine.core.errors.exceptions.jdbc;

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
