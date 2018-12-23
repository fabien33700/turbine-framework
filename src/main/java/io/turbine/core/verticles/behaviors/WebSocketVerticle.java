package io.turbine.core.verticles.behaviors;

import io.reactivex.Observable;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.WsConnection;

/**
 * Defines the behavioral specification of a Web Socket Verticle,
 * which means a verticle that provides asynchronous messages handling capabilities
 * through the Web Socket protocol (RFC 6455).
 * A WebSocketVerticle must provide a reactive source of typed messages.
 *
 * Note that WS verticle is divided into two specifications : a ladder to
 * manage client connections and to dispatch them into rooms verticles that
 * manage messaging and broadcasting.
 *
 * @see Message the interface that defines the structure of a WS Message
 * @see WebSocketLadder
 * @see WebSocketRoom
 * @param <S> The type that identifies the message sender
 * @param <B> The type of the message Body
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface WebSocketVerticle<S, B> extends Verticle {

    /**
     * Send a message to all the clients.
     * @param message The message to broadcast
     */
    void broadcast(Message<S, B> message);

    /**
     * The reactive observable source of new client connections.
     * @return An Observable of WsConnection
     */
    Observable<WsConnection<S>> connections();

    /**
     * The reactive observable source of client disconnections.
     * Note that he ServerWebSocket connection wrapped into the emitted WsConnection
     * is not longer active.
     * @return An Observable of WsConnection
     */
    Observable<WsConnection<S>> disconnections();

    /**
     * The reactive observable source of Message objects.
     * @return An Observable of Message
     */
    Observable<Message<S, B>> messages();

}
