package io.turbine.core.verticles.behaviors;

import io.reactivex.Completable;
import io.turbine.core.errors.exceptions.ws.WebSocketException;
import io.turbine.core.ws.WsConnection;

/**
 * A WebSocket messaging room.
 * The room is in charge of :
 *   - receiving and brodcasting messages of clients to each other
 *   - notifying the ladder for idle users
 *   - notifying the ladder for being empty
 *
* @param <S> The type of the object that identifies the message (S)ender
 * @param <R> The type of the object that identifies (R)oom
 * @param <B> The type of the message (B)ody
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface WebSocketRoom<S, R, B> extends WebSocketVerticle<S, B> {

    /**
     * The end-of-life signal. The associated ladder subscribes to it
     * at room creation and will destroy the room once the signal is completed.
     * @return A Completable instance
     */
    Completable emptySignal();

    /**
     * A reference to the parent ladder.
     * @return A webSocketLadder instance
     */
    WebSocketLadder<S, R, B> getLadder();

    /**
     * Sends the WebSocket connections to the room.
     * @param connection The WS client connection
     */
    void connect(WsConnection<S> connection) throws WebSocketException;

    /**
     * Set the capacity of the room, which means the maximum
     * amount of client that can connects simultaneously to it.
     * A 0 value is equivalent to "infinity"
     * @param capacity The maximum number of users
     */
    void setCapacity(long capacity);

    /**
     * Returns the room capacity, the maximum number of clients.
     * @return The room capacity.
     */
    long capacity();

    /**
     * Returns the number of clients that are actually connected to the room.
     * @return the room occupation
     */
    long occupation();

    /**
     * Returns the total count of messages sent in the room.
     * @return The messages count
     */
    long messagesCount();

    /**
     * The room identifier.
     * @return The identifier
     */
    R identifier();
}
