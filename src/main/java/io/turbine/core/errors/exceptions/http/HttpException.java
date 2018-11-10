package io.turbine.core.errors.exceptions.http;

import io.turbine.core.json.JsonBuilder;
import io.turbine.core.json.JsonSerializable;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

import static io.netty.handler.codec.http.HttpResponseStatus.valueOf;
import static io.turbine.core.json.JsonBuilder.create;

/**
 * The base exception for representing a HTTP error that can be sended back to the client.
 * Implementations of this class must be serializable in at least one format.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public abstract class HttpException extends RuntimeException implements JsonSerializable {

    /**
     * Defines the default message of this exception type.
     * @return The default message
     */
    protected abstract String defaultMessage();

    /**
     * The default HTTP status code of this exception type.
     * @return The default status code
     */
    public abstract int statusCode();

    /**
     * The instant of the exception creation.
     */
    private final Instant instant = Instant.now();

    /**
     * Constructs a HttpException (default constructor)
     */
    protected HttpException() {
        super();
    }

    /**
     * Constructs a HttpException with the specified message.
     * @param message The exception message
     */
    protected HttpException(String message) {
        super(message);
    }

    /**
     * Constructs a HttpException with the specified cause.
     * @param cause The error cause
     */
    protected HttpException(Throwable cause) {
        super(null, cause);
    }

    /**
     * Gets the exception message, or the default message if thre is no one.
     * @return The exception message or default message
     */
    @Override
    public String getMessage() {
        return super.getMessage() != null ? super.getMessage() : defaultMessage();
    }

    /**
     * Gets the exception creation instant.
     * @return The exception creation instant
     */
    public Instant getInstant() {
        return instant;
    }

    @Override
    public JsonObject toJson() {
        JsonBuilder builder = create()
                .put("message", getMessage())
                .put("type", getClass().getSimpleName())
                .put("instant", getInstant())
                .put("code", statusCode())
                .put("reason", valueOf(statusCode()).reasonPhrase());

        if (this instanceof ServerErrorException)
            builder.put("uuid", ((ServerErrorException) this).getUuid());

        return builder.build();
    }
}
