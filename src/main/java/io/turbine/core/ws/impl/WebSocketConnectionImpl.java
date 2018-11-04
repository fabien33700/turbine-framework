package io.turbine.core.ws.impl;

import io.turbine.core.ws.WebSocketConnection;
import io.vertx.reactivex.core.http.ServerWebSocket;

import java.time.Instant;
import java.util.Objects;

public final class WebSocketConnectionImpl<S> implements WebSocketConnection<S> {

    private final S sender;
    private final ServerWebSocket webSocket;
    private final Instant openingTime;
    private Instant lastActivityTime;

    public WebSocketConnectionImpl(S sender, ServerWebSocket webSocket) {
        this.sender = sender;
        this.webSocket = webSocket;
        this.openingTime = Instant.now();
        this.lastActivityTime = Instant.now();
    }

    @Override
    public S sender() {
        return sender;
    }

    @Override
    public ServerWebSocket webSocket() {
        return webSocket;
    }

    @Override
    public Instant openingTime() {
        return openingTime;
    }

    @Override
    public Instant lastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(Instant lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }


    @Override
    public int hashCode() {
        return Objects.hash(webSocket, sender);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WebSocketConnection &&
            ((WebSocketConnection) obj).webSocket().equals(webSocket());
    }
}
