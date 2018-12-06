package io.turbine.core.ws.impl;

import io.turbine.core.ws.Message;
import io.turbine.core.ws.WsConnection;

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
    public void respond(B responseBody) {
        connection.webSocket().writeTextMessage(responseBody.toString());
    }

    @Override
    public Instant sentAt() {
        return sentAt;
    }

    @Override
    public B body() {
        return body;
    }
}
