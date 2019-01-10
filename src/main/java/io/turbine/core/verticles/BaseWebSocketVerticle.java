package io.turbine.core.verticles;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import io.turbine.core.utils.rxcollection.ReactiveList;
import io.turbine.core.utils.rxcollection.events.ListEvent;
import io.turbine.core.utils.rxcollection.impl.ReactiveListImpl;
import io.turbine.core.verticles.behaviors.WebSocketVerticle;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.WsConnection;

public abstract class BaseWebSocketVerticle<S, R, B> extends BaseVerticle
        implements WebSocketVerticle<S, B>
{
    protected final ReactiveList<WsConnection<S>> connections = new ReactiveListImpl<>();

    // FIXME Messages here will be send to everyone, use with caution !
    protected final Subject<Message<S,B>> messages = BehaviorSubject.create();

    @Override
    public Observable<Message<S, B>> messages() {
        return messages;
    }

    @Override
    public Observable<WsConnection<S>> connections() {
        return connections.getObserver().additions()
                .map(ListEvent::first);
    }

    @Override
    public Observable<WsConnection<S>> disconnections() {
        return connections.getObserver().deletions()
                .map(ListEvent::first);
    }

    @Override
    public void broadcast(final Message<S, B> message) {
        connections.forEach(
            conn -> conn.webSocket().writeTextMessage(
                    message.toJsonString()
            ));
    }
}
