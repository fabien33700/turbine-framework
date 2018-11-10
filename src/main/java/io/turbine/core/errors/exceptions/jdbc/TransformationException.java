package io.turbine.core.errors.exceptions.jdbc;

/**
 * This exception can occurred when the call of a ResultTransformer by a Results implementation
 * had thrown itself an exception.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class TransformationException extends JdbcException {
    /**
     * The constant holding the exception defaut message
     */
    private static final String DEFAULT_MESSAGE = "Could not transform raw results to a model instance.";

    public TransformationException() {
        super(DEFAULT_MESSAGE);
    }

    public TransformationException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
