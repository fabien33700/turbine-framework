package io.turbine.core.verticles;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import io.turbine.core.utils.rxcollection.ReactiveMap;
import io.turbine.core.utils.rxcollection.impl.ReactiveMapImpl;
import io.turbine.core.utils.rxcollection.observers.ReactiveMapObserver;
import io.turbine.core.verticles.behaviors.WebSocketVerticle;
import io.turbine.core.verticles.behaviors.WebVerticle;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.WsConnection;
import io.turbine.core.ws.impl.WsConnectionImpl;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.ServerWebSocket;

import static io.turbine.core.utils.Utils.MapBuilder.mapOf;
import static java.util.Objects.requireNonNull;

public abstract class WebSocketLadder<S, R, B> extends BaseHttpVerticle implements WebSocketVerticle<S, B> {

    private ReactiveMap<R, WebSocketRoom<S, R, B>> rooms = new ReactiveMapImpl<>();

    protected final Subject<Message<S, B>> messages = BehaviorSubject.create();

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

        try {
            R roomId = getRoomIdentifier(ws);
            WebSocketRoom<S, R, B> room = roomFactory().apply(roomId);

            register(
                    room.rxStart().subscribe(() -> rooms.put(roomId, room)) );
        } catch (Exception ex) {
            // FIXME
            System.err.println("Error deploying the room");
            ex.printStackTrace();
        }

       // ws.closeHandler((v) -> connections.remove(conn));
    }

    protected abstract boolean accepts(ServerWebSocket ws);

    protected abstract S getRequestSender(ServerWebSocket ws) throws Exception;

    protected abstract R getRoomIdentifier(ServerWebSocket ws) throws Exception;

    protected abstract Function<R, WebSocketRoom<S, R, B>> roomFactory();

    protected ReactiveMapObserver<R, WebSocketRoom<S, R, B>> getRoomsObserver() {
        return rooms.getObserver();
    }

   /* @Override
    public void broadcast(final Message<S, B> message) {
        connections.forEach(
            conn -> conn.webSocket().writeTextMessage(
                    message.toJsonString()
            ));
    }*/

    /*@Override
    public Observable<Message<S, B>> messages() {
        return messages;
    }*/

}
