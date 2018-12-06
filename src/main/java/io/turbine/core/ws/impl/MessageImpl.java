package io.turbine.core.ws.impl;

import io.turbine.core.json.JsonBuilder;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.WsConnection;
import io.vertx.core.json.JsonObject;

import java.time.Instant;

public final class MessageImpl<S, B> implements Message<S, B> {

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
        return JsonBuilder.json(
            new String[] { "sender", "sentAt", "body" },
            new Object[] { sender(), sentAt(), body() }
        );
    }
}
