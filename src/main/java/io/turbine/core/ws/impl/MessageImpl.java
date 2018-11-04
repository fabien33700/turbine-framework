package io.turbine.core.ws.impl;

import io.turbine.core.ws.Message;

import java.time.Instant;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public final class MessageImpl<S, B> implements Message<S, B> {

    private final S sender;
    private final B body;
    private final Instant sentAt;

    public MessageImpl(B body) {
        this(null, body);
    }

    public MessageImpl(S sender, B body) {
        this.sender = sender;
        this.body = body;
        this.sentAt = Instant.now();
    }

    @Override
    public Optional<S> sender() {
        return ofNullable(sender);
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
