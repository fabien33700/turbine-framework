package io.turbine.core.verticles.behaviors;

import io.turbine.core.utils.rxcollection.observers.ReactiveMapObserver;

public interface WebSocketLadder<S, R, B> extends WebSocketVerticle<S, B> {

    ReactiveMapObserver<R, WebSocketRoom<S, R, B>> getRoomsObserver();

    boolean allowAnonymous();

}
