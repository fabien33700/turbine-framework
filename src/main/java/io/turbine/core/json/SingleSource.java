package io.turbine.core.json;

import io.vertx.core.json.JsonObject;

public class SingleSource implements JsonSource {

    private final JsonObject object;

    SingleSource(JsonObject object) {
        this.object = object;
    }

    <T> SingleSource(T object) {
        this.object = JsonObject.mapFrom(object);
    }

    @Override
    public String encode() {
        return object.encode();
    }

    @Override
    public String encodePrettily() {
        return object.encodePrettily();
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public <T> T mapTo(Class<T> clazz) {
        return object.mapTo(clazz);
    }

    @Override
    public <T> Iterable<T> mapCollectionTo(Class<T> clazz) {
        throw new UnsupportedOperationException("SingleSource does not supports collection operation.");
    }

}
