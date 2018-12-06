package io.turbine.core.ws.codec;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;

import static io.vertx.reactivex.core.buffer.Buffer.buffer;

public class JsonCodec implements Codec<JsonObject> {

    @Override
    public Buffer encode(JsonObject object) {
        return buffer(object.encode());
    }

    @Override
    public JsonObject decode(Buffer buffer) {
        return buffer.toJsonObject();
    }
}
