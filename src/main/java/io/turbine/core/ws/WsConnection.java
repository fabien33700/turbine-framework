package io.turbine.core.ws;

import io.turbine.core.json.JsonSerializable;
import io.vertx.reactivex.core.http.ServerWebSocket;

import java.time.Instant;

/**
 * Represents the basic structure for a WebSocket connection.
 * This class wraps the underlying ServerWebSocket provided by Vert.x, with
 * some additional informations such as a sender identifier (could be a String representing
 * a nickname or a complex object for identifying/storing user in the system, and
 * the instant of connection opening and of last activity.
 * @param <S> The type for representing the message sender
 */
public interface WsConnection<S extends JsonSerializable> {

    S sender();
    ServerWebSocket webSocket();
    Instant openingTime();
    Instant lastActivityTime();
}
