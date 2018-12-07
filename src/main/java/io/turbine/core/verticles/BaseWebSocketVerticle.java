package io.turbine.core.verticles;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import io.turbine.core.utils.rxcollection.ReactiveList;
import io.turbine.core.utils.rxcollection.ReactiveListImpl;
import io.turbine.core.utils.rxcollection.ReactiveListObserver;
import io.turbine.core.verticles.behaviors.WebSocketVerticle;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.WsConnection;
import io.turbine.core.ws.codec.Codec;
import io.turbine.core.ws.codec.ModelCodec;
import io.turbine.core.ws.impl.MessageImpl;
import io.turbine.core.ws.impl.WsConnectionImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.ServerWebSocket;

public abstract class BaseWebSocketVerticle<S, B> extends BaseHttpVerticle implements WebSocketVerticle<S, B> {

    protected final ReactiveList<WsConnection<S>> connections = new ReactiveListImpl<>();
    protected final Subject<Message<S, B>> messages = BehaviorSubject.create();
    protected final Codec<B> codec = new ModelCodec<>(this::readBody);

    @Override
    public final Single<HttpServer> listen(int port) {
        return httpServer()
            .websocketHandler(this::handleWebSocket)
            .rxListen(port);
    }

    private void handleWebSocket(ServerWebSocket ws) {
        try {
            if (!accepts(ws))
                throw new IllegalStateException("connection does not fulfill server preconditions");

            S sender = getRequestSender(ws);
            if (!allowAnonymous() && sender == null)
                throw new UnsupportedOperationException("anonymous connection is not allowed");

            handleWebSocketConnection(ws, sender);
        } catch (Exception ex) {
            logger.debug("Client connection from {} rejected : {}", ws.remoteAddress(), ex);
            ws.reject();
        }
    }

    @Override
    public boolean allowAnonymous() {
        return false;
    }

    @SuppressWarnings("unchecked")
    private void handleWebSocketConnection(ServerWebSocket ws, S sender) {
        WsConnection<S> conn = new WsConnectionImpl<>(sender, ws);
        connections.add(conn);

        ws.textMessageHandler(raw -> {
            try {
                messages.onNext(
                        new MessageImpl<>(conn, codec.decode(raw)));
            } catch (Exception e) {
                logger.debug("Could not decode client message", e);
            }
        });

        ws.closeHandler((v) -> connections.remove(conn));
    }

    protected abstract boolean accepts(ServerWebSocket ws);

    protected abstract S getRequestSender(ServerWebSocket ws) throws Exception;

    protected abstract B readBody(JsonObject json) throws Exception;

    @Override
    public Observable<Message<S, B>> messages() {
        return messages;
    }

    public ReactiveListObserver<WsConnection<S>> connections() {
        // Returning only observer to prevent user from accessing connections list !
        return connections.getObserver();
    }


}
