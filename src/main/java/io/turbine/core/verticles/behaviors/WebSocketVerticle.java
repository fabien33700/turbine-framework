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
 * @see Message the interface that defines the structure of a WS Message
 * @param <S> The type that identifies the message sender
 * @param <B> The type of the message Body
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface WebSocketVerticle<S, B>  {

    void broadcast(Message<S, B> message);

    /**
     * The reactive observable source of Message objects.
     * @return An Observable of Message
     */
    Observable<WsConnection<S>> connections();

    Observable<WsConnection<S>> disconnections();

    Observable<Message<S, B>> messages();

}
