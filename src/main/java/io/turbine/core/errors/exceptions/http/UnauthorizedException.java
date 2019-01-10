package io.turbine.core.errors.exceptions.http;

/**
 * This exception represents the 401 - 'Unauthorized' HTTP Error.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class UnauthorizedException extends HttpException {

    /**
     * The constant holding the exception defaut message
     */
    private static final String DEFAULT_MESSAGE = "You have to log yourself in to use this service.";

    public UnauthorizedException() {
        super(DEFAULT_MESSAGE);
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    protected String defaultMessage() {
        return DEFAULT_MESSAGE;
    }

    @Override
    public final int statusCode() {
        return 401;
    }
}
