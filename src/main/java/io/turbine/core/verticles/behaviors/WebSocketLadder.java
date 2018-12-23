package io.turbine.core.verticles.behaviors;

import io.turbine.core.utils.rxcollection.observers.ReactiveMapObserver;

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

}
