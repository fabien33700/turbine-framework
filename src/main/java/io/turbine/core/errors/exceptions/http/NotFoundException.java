package io.turbine.core.errors.exceptions.http;

/**
 * This exception represents the 404 - 'Not Found' HTTP Error.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class NotFoundException extends HttpException {

    /**
     * The constant holding the exception defaut message
     */
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
