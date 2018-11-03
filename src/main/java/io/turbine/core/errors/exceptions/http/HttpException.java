package io.turbine.core.errors.exceptions.http;

import io.turbine.core.json.JsonBuilder;
import io.turbine.core.json.JsonSerializable;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

import static io.netty.handler.codec.http.HttpResponseStatus.valueOf;
import static io.turbine.core.json.JsonBuilder.create;

public abstract class HttpException extends RuntimeException implements JsonSerializable {

    protected abstract String defaultMessage();

    public abstract int statusCode();

    private final Instant instant = Instant.now();

    protected HttpException() {
        super();

    }

    protected HttpException(String message) {
        super(message);
    }

    protected HttpException(Throwable cause) {
        super(null, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage() != null ? super.getMessage() : defaultMessage();
    }

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
