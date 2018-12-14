package io.turbine.core.verticles.behaviors;

import io.reactivex.Completable;
import io.turbine.core.verticles.support.Startable;
import io.turbine.core.ws.WsConnection;

public interface WebSocketRoom<S, R, B> extends WebSocketVerticle<S, B>, Startable {
    Completable endOfLifeSignal();

    WebSocketLadder<S, R, B> getLadder();

    void connect(WsConnection<S> connection);

    void clearCapacity();

    void setCapacity(long capacity);

    long capacity();

    long occupation();

    R identifier();
}
