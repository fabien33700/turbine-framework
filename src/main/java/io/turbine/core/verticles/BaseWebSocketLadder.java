package io.turbine.core.verticles;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.turbine.core.errors.exceptions.http.UnauthorizedException;
import io.turbine.core.utils.rxcollection.ReactiveMap;
import io.turbine.core.utils.rxcollection.impl.ReactiveMapImpl;
import io.turbine.core.utils.rxcollection.observers.ReactiveMapObserver;
import io.turbine.core.verticles.behaviors.HttpVerticle;
import io.turbine.core.verticles.behaviors.WebSocketLadder;
import io.turbine.core.verticles.behaviors.WebSocketRoom;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.RoomFactory;
import io.turbine.core.ws.WsConnection;
import io.turbine.core.ws.impl.WsConnectionImpl;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.ServerWebSocket;

import java.util.concurrent.TimeUnit;

import static io.reactivex.Completable.fromSingle;
import static io.turbine.core.deployment.VerticleFactory.factory;

public abstract class BaseWebSocketLadder<S, R, B> extends BaseWebSocketVerticle<S, R, B>
        implements WebSocketLadder<S, R, B>, HttpVerticle
{
    private static final long KEEP_IDLE_ROOMS = 5 * 1000;

    class DelegateHttpVerticle extends BaseHttpVerticle {
        @Override
        public final Single<HttpServer> listen(int port) {
            return httpServer()
                    .websocketHandler(BaseWebSocketLadder.this::handleWebSocket)
                    .rxListen(port);
        }

        @Override
        public void init(Vertx vertx, Context context) {
            super.init(vertx, context);
            setLoggerName(BaseWebSocketLadder.this.getClass().getSimpleName()
                + "'s HTTP server");
        }
    }

    private HttpVerticle http;

    private final ReactiveMap<R, WebSocketRoom<S, R, B>> rooms = new ReactiveMapImpl<>();

    private void rejectConnection(ServerWebSocket ws, Throwable cause) {
        logger.debug("Client connection from {} rejected : {}", ws.remoteAddress(), cause.getMessage());

        if (cause instanceof UnauthorizedException) {
            ws.writeTextMessage(cause.getMessage());
            ws.close();
        } else {
            ws.reject();
        }
    }

    private void handleWebSocket(ServerWebSocket ws) {
        if (allowAnonymous()) {
            handleWebSocketConnection(ws, null);
        } else {
            register(accepts(ws).subscribe(
                sender -> {
                    try {
                        if (sender == null)
                            throw new UnsupportedOperationException("anonymous connections are not allowed");

                        handleWebSocketConnection(ws, sender);
                    } catch (Exception ex) {
                        rejectConnection(ws, ex);
                    }
                },
                ex -> rejectConnection(ws, ex)
            ));
        }
    }

    @Override
    public boolean allowAnonymous() {
        return false;
    }

    private void handleWebSocketConnection(ServerWebSocket ws, S sender) {
        WsConnection<S> conn = new WsConnectionImpl<>(sender, ws);

        try {
            R roomId = getRoomIdentifier(ws);
            WebSocketRoom<S, R, B> room;

            if (rooms.containsKey(roomId)) {
                room = rooms.get(roomId);
            } else {
                room = roomFactory().apply(this, roomId);
                /* FIXME:  use deployer to deploy room verticle */
               // deployVerticle(roomFactory())
                register(
                        room.disconnections().subscribe(connections::remove),
                        room.rxStart().subscribe(() -> rooms.put(roomId, room)),
                        room.emptySignal()
                                .delay(keepIdleRooms(), TimeUnit.MILLISECONDS)
                                .subscribe(() -> this.clearRoom(roomId))
                );
            }
            room.connect(conn);
        } catch (Exception ex) {
            logger.error("Cannot open the room.", ex);
        }
    }

    /* FIXME : Use deployer to undeploy room child verticle */
    private void clearRoom(R roomId) {
        logger.info("Room {} was empty for {} ms so it was destroyed.", roomId.toString(), KEEP_IDLE_ROOMS);
        try {
            rooms.get(roomId).rxStop()
                    .subscribe(() -> rooms.remove(roomId));
        } catch (Exception ex) {
            logger.error("The verticle for room {} could not stop properly", roomId);
        }
    }

    @Override
    public Completable rxStart() {
        return super.rxStart().concatWith(
                /** FIXME: Call to the deployer **/
            fromSingle(
                deployer().deployVerticle(
                        factory(DelegateHttpVerticle::new, DelegateHttpVerticle.class), config())
            ));
    }

    @Override
    public ReactiveMapObserver<R, WebSocketRoom<S, R, B>> getRoomsObserver() {
        return rooms.getObserver();
    }

    protected abstract R getRoomIdentifier(ServerWebSocket ws) throws Exception;

    protected abstract RoomFactory<S, R, B> roomFactory();

    @Override
    public long keepIdleRooms() {
        return KEEP_IDLE_ROOMS;
    }

    @Override
    public Observable<Message<S, B>> messages() {
        // FIXME Use this to broadcast announces to all the rooms
        return Observable.never();
    }

    /* ********************************* *
     * Delegate method for HttpVerticle  *
     * ********************************* */
    @Override
    public Single<HttpServer> listen(int port) {
        return http.listen(port);
    }

    @Override
    public boolean useSsl() {
        return http.useSsl();
    }

    @Override
    public int port() {
        return http.port();
    }

    @Override
    public HttpServer httpServer() {
        return http.httpServer();
    }

    @Override
    public HttpServerOptions httpServerOptions() {
        return http.httpServerOptions();
    }

    @Override
    public JksOptions jksOptions() {
        return http.jksOptions();
    }

    @Override
    public Single<HttpServer> listen() {
        return http.listen();
    }
}
