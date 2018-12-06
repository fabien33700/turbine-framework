package io.turbine.core.errors.exceptions.http;

import java.util.UUID;

import static io.turbine.core.utils.Utils.Strings.getStackTraceAsString;
import static java.util.UUID.randomUUID;

/**
 * This exception represents 500 - 'Internal Server Error' HTTP Error.
 * In order to hide the source exception detail to the user, the class wraps
 * it and serialize a generic HTTP error that will be sended back to the client.
 *
 * The class has a unique identifier that will be logged with the source exception,
 * so administrator can find its stacktrace with the uuid given to the client.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class ServerErrorException extends HttpException {

    /**
     * The exception unique identifier
     */
    private final UUID uuid;

    public ServerErrorException(Throwable cause) {
        super(cause);
        this.uuid = randomUUID();
    }

    @Override
    protected String defaultMessage() {
        return "An internal server error has occurred. " +
                "Please contact your administrator and supply him the error unique identifier.";
    }

    @Override
    public int statusCode() {
        return 500;
    }

    /**
     * Gets the exception unique identifier.
     * @return The exception uuid
     */
    public String getUuid() {
        return uuid.toString();
    }

    @Override
    public String toString() {
        return getMessage() + "\n***** Server error unique identifier : {" + getUuid() + "}\n" +
                "***** Error cause stacktrace : \n" + getStackTraceAsString(getCause());
    }
}
