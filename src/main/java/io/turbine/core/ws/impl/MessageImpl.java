package io.turbine.core.ws.impl;

import io.turbine.core.json.JsonFormat;
import io.turbine.core.json.JsonSerializable;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.WsConnection;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

public final class MessageImpl<S extends JsonSerializable, B> implements Message<S, B> {

    private final B body;
    private final WsConnection<S> connection;
    private final Instant sentAt;

    public MessageImpl(WsConnection<S> connection, B body) {
        this.connection = connection;
        this.body = body;
        this.sentAt = Instant.now();
    }

    @Override
    public S sender() {
        return connection.sender();
    }

    @Override
    public Instant sentAt() {
        return sentAt;
    }

    @Override
    public B body() {
        return body;
    }

    @Override
    public JsonObject toJson() {
        return JsonFormat.Builder.create()
                .put("sender", sender().toJson())
                .put("sentAt", sentAt)
                .put("body", body)
            .build();
    }
}
