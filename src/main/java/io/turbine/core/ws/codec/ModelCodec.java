package io.turbine.core.ws.codec;

import io.reactivex.functions.Function;
import io.turbine.core.json.JsonBuilder;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import static java.util.Objects.requireNonNull;

public class ModelCodec<M> implements Codec<M> {

    private final Function<JsonObject, M> unmarshallFn;

    public ModelCodec(Function<JsonObject, M> unmarshallFn) {
        requireNonNull(unmarshallFn, "unmarshalling Json to model function");
        this.unmarshallFn = unmarshallFn;
    }

    @Override
    public String encode(M target) throws Exception {
        return Json.encode(target);
    }

    @Override
    public M decode(String source) throws Exception {
        return unmarshallFn.apply(JsonBuilder.fromString(source));
    }
}
