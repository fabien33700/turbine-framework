package io.turbine.core.verticles.behaviors;

import io.reactivex.Single;
import io.turbine.core.utils.rxcollection.observers.ReactiveMapObserver;
import io.vertx.reactivex.core.http.ServerWebSocket;

/**
 * A WebSocket messaging ladder.
 * The ladder is in charge of :
 *   - handling and managing client connections
 *   - dispatching connections into room according some defined criteria
 *   - managing the lifecycle of rooms it has created
 *
* @param <S> The type of the object that identifies the message (S)ender
 * @param <R> The type of the object that identifies (R)oom
 * @param <B> The type of the message (B)ody
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface WebSocketLadder<S, R, B> extends WebSocketVerticle<S, B> {

    /**
     * The map observer of the ladder rooms pool.
     * @return A ReactiveMapObserver
     */
    ReactiveMapObserver<R, WebSocketRoom<S, R, B>> getRoomsObserver();

    /**
     * Indicates whether the current ladder allow or deny anonymous connections.
     * @return true to allow, false to deny
     */
    boolean allowAnonymous();

    /**
     * Asynchronously check the acceptance of the given low-level connection
     * for the ladder.
     * The method must be able to determine the sender from the connection.
     * @param ws The WebSocket connection to check
     * @return A single that will emits the sender identifier object, or
     *   throws an error if the connection cannot be accepted.
     */
    Single<S> accepts(final ServerWebSocket ws);

    /**
     * Returns the delay in milliseconds after which an idle room
     * (that not holds any users anymore) will be automatically destroyed by the ladder.
     * @return A delay in ms
     */
    long keepIdleRooms();
}
