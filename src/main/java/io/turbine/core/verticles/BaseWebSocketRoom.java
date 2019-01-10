package io.turbine.core.verticles;

import io.reactivex.Completable;
import io.turbine.core.errors.exceptions.ws.WebSocketException;
import io.turbine.core.json.JsonFormat;
import io.turbine.core.verticles.behaviors.WebSocketLadder;
import io.turbine.core.verticles.behaviors.WebSocketRoom;
import io.turbine.core.ws.Message;
import io.turbine.core.ws.WsConnection;
import io.turbine.core.ws.impl.MessageImpl;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

import static io.turbine.core.utils.Utils.Reactive.completeOnFirst;
import static java.util.Objects.requireNonNull;

public abstract class BaseWebSocketRoom<S, R, B>
        extends BaseWebSocketVerticle<S, R, B> implements WebSocketRoom<S, R, B> {

    protected final WebSocketLadder<S, R, B> ladder;

    private final R roomIdentifier;

    private long capacity = 0;

    private long messagesCount = 0;

    protected BaseWebSocketRoom(WebSocketLadder<S, R, B> ladder, R roomIdentifier) {
        super();
        requireNonNull(roomIdentifier, "room's identifier");
        this.ladder = ladder;
        this.roomIdentifier = roomIdentifier;
        initialize();
    }

    private void handleWebSocketConnection(final WsConnection<S> conn) {
        conn.webSocket().textMessageHandler(text -> {
            try {
                final JsonObject json = JsonFormat.fromString(text);
                final Message<S, B> message = new MessageImpl<>(conn, parseMessage(json));
                messages.onNext(message);
            } catch (DecodeException ignored) {}
        });

        conn.webSocket().closeHandler(v -> connections.remove(conn));
    }

    private void initialize() {
        register(
                connections().subscribe(this::handleWebSocketConnection),
                messages().subscribe(this::broadcast),
                messages().subscribe(m -> messagesCount++));
    }

    @Override
    public long messagesCount() {
        return messagesCount;
    }

    protected abstract B parseMessage(final JsonObject source);

    @Override
    public void connect(WsConnection<S> connection) throws WebSocketException {
        if (capacity > 0 && occupation() >= capacity) {
            throw new WebSocketException("The room " + roomIdentifier + " is full.");
        }
        connections.add(connection);
    }

    @Override
    public Completable emptySignal() {
        return completeOnFirst(disconnections().filter(e -> connections.isEmpty()));
    }

    @Override
    public WebSocketLadder<S, R, B> getLadder() {
        return ladder;
    }

    @Override
    public void setCapacity(long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be a positive integer");
        }
        this.capacity = capacity;
    }

    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public long occupation() {
        return connections.size();
    }

    @Override
    public R identifier() {
        return roomIdentifier;
    }
}
