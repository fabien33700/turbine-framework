package io.turbine.core.verticles;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.turbine.core.utils.rxcollection.ReactiveList;
import io.turbine.core.utils.rxcollection.ReactiveMap;
import io.turbine.core.utils.rxcollection.events.ListEvent;
import io.turbine.core.utils.rxcollection.impl.ReactiveListImpl;
import io.turbine.core.utils.rxcollection.impl.ReactiveMapImpl;
import io.turbine.core.utils.rxcollection.observers.ReactiveMapObserver;
import io.turbine.core.verticles.behaviors.HttpVerticle;
import io.turbine.core.verticles.behaviors.WebSocketLadder;
import io.turbine.core.verticles.behaviors.WebSocketRoom;
import io.turbine.core.verticles.support.RoomFactory;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.WsConnection;
import io.turbine.core.ws.impl.WsConnectionImpl;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.ServerWebSocket;

// FIXME : Ladder and Room have similar behaviors (connections, messages, broadcast) but
// Ladder is an httpserver (BaseHttpVerticle) whereas rooms are not.
// Multiple inheritance from BaseHttpVerticle and a common class BaseWebSocketVerticle is impossible
public abstract class BaseWebSocketLadder<S, R, B> extends BaseHttpVerticle
        implements WebSocketLadder<S, R, B>, HttpVerticle
{
    private final ReactiveMap<R, WebSocketRoom<S, R, B>> rooms = new ReactiveMapImpl<>();

    protected final ReactiveList<WsConnection<S>> connections = new ReactiveListImpl<>();

    // Messages here will be send to everyone, use with caution !
    private final Observable<Message<S,B>> messages = BehaviorSubject.create();

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

            WebSocketRoom<S, R, B> room;
            if (rooms.containsKey(roomId)) {
                room = rooms.get(roomId);
            } else {
                room = createWebSocketRoom(roomId);
            }
            room.connect(conn);
        } catch (Exception ex) {
            // FIXME
            System.err.println("Error deploying the room");
            ex.printStackTrace();
        }
    }

    private WebSocketRoom<S, R, B> createWebSocketRoom(R roomId) throws Exception {
        WebSocketRoom<S, R, B> room = roomFactory().apply(this, roomId);
        register(
            room.disconnections().subscribe(connections::remove),
            room.rxStart().subscribe(() -> rooms.put(roomId, room))
        );
        return room;
    }

    protected boolean accepts(ServerWebSocket ws) {
        try {
            return getRoomIdentifier(ws) != null;
        } catch (Exception e) {
            return false;
        }
    }

    protected abstract S getRequestSender(ServerWebSocket ws) throws Exception;

    protected abstract R getRoomIdentifier(ServerWebSocket ws) throws Exception;

    protected abstract RoomFactory<S, R, B> roomFactory();

    public ReactiveMapObserver<R, WebSocketRoom<S, R, B>> getRoomsObserver() {
        return rooms.getObserver();
    }

    @Override
    public void broadcast(final Message<S, B> message) {
        connections.forEach(
            conn -> conn.webSocket().writeTextMessage(
                    message.toJsonString()
            ));
    }

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
}
