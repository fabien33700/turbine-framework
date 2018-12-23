package io.turbine.core.ws;

import io.reactivex.functions.BiFunction;
import io.turbine.core.verticles.behaviors.WebSocketLadder;
import io.turbine.core.verticles.behaviors.WebSocketRoom;

/**
 * Defines a function that create a WebSocketRoom instance from
 * a room identifier and a ladder.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
@FunctionalInterface
public interface RoomFactory<S, R, B> extends
        BiFunction<WebSocketLadder<S, R, B>, R, WebSocketRoom<S, R, B>> {}
