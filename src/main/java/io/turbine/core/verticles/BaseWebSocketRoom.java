package io.turbine.core.verticles;

import io.turbine.core.verticles.behaviors.WebSocketVerticle;

import static java.util.Objects.requireNonNull;

public abstract class WebSocketRoom<S, R, B> extends BaseVerticle {

    private final R roomIdentifier;

    protected WebSocketRoom(R roomIdentifier) {
        requireNonNull(roomIdentifier, "room's identifier");
        this.roomIdentifier = roomIdentifier;
    }

}
