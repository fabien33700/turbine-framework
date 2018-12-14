package io.turbine.core.verticles.support;

import io.reactivex.functions.BiFunction;
import io.turbine.core.verticles.behaviors.WebSocketLadder;
import io.turbine.core.verticles.behaviors.WebSocketRoom;

@FunctionalInterface
public interface WebSocketRoomFactory<S, R, B> extends
        BiFunction<WebSocketLadder<S, R, B>, R, WebSocketRoom<S, R, B>>
{
}
