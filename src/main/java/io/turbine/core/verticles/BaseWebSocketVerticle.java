package io.turbine.core.verticles;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import io.turbine.core.verticles.behaviors.WebSocketVerticle;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.WebSocketConnection;
import io.turbine.core.ws.impl.MessageImpl;
import io.turbine.core.ws.impl.WebSocketConnectionImpl;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.ServerWebSocket;


public abstract class BaseWebSocketVerticle<S, B> extends BaseHttpVerticle implements WebSocketVerticle<S, B> {

    protected final Subject<WebSocketConnection<S>> connections = BehaviorSubject.create();
    protected final Subject<Message<S, B>> messages = BehaviorSubject.create();

    @Override
    public final Single<HttpServer> listen(int port) {
        return httpServer()
            .websocketHandler(this::handleWebSocket)
            .rxListen(port);
    }

    private void handleWebSocket(ServerWebSocket ws) {
        if (accepts(ws)) {
            try {
                S sender = getRequestSender(ws);
                handleWebSocketConnection(ws, sender);
            } catch (Exception ex) {
                // Unauthorized
                ws.reject();
            }
        } else {
            logger.debug("Connection rejected");
            ws.reject();
        }
    }

    private void handleWebSocketConnection(ServerWebSocket ws, S sender) {
        connections.onNext(new WebSocketConnectionImpl<>(sender, ws));
        ws.textMessageHandler(text -> messages.onNext(
            new MessageImpl<>(sender, readBody(text)))
        );
    }

    protected abstract boolean accepts(ServerWebSocket ws);

    protected abstract S getRequestSender(ServerWebSocket ws) throws Exception;

    protected abstract B readBody(String rawMessage);

    @Override
    public Observable<Message<S, B>> messages() {
        return messages;
    }

}
