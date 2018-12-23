package io.turbine.core.errors.exceptions.ws;

import io.turbine.core.json.JsonFormat;
import io.turbine.core.json.JsonSerializable;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

import static io.turbine.core.json.JsonFormat.Builder.create;

/**
 * The base exception for representing a HTTP error that can be sended back to the client.
 * Implementations of this class must be serializable in at least one format.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class WebSocketException extends RuntimeException implements JsonSerializable {

    /**
     * The instant of the exception creation.
     */
    private final Instant instant = Instant.now();

    /**
     * Constructs a WebSocketException (default constructor)
     */
    public WebSocketException() {
        super();
    }

    /**
     * Constructs a WebSocketException with the specified message.
     * @param message The exception message
     */
    public WebSocketException(String message) {
        super(message);
    }

    /**
     * Constructs a WebSocketException with the specified cause.
     * @param cause The error cause
     */
    public WebSocketException(Throwable cause) {
        super(null, cause);
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
        JsonFormat.Builder builder = create()
                .put("message", getMessage())
                .put("type", getClass().getSimpleName())
                .put("instant", getInstant());

        return builder.build();
    }
}
