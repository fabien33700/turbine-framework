package io.turbine.core.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface JsonSource {
    String encode();
    String encodePrettily();
    boolean isCollection();

    <T> T mapTo(Class<T> clazz);
    <T> Iterable<T> mapCollectionTo(Class<T> clazz);

    static JsonSource from(final JsonObject object) {
        return new SingleSource(object);
    }

    static JsonSource from(Object object) {
        return new SingleSource(object);
    }

    static JsonSource from(final JsonArray array) {
        return new MultipleSource(array);
    }

    static <T> JsonSource from(Iterable<T> iterable) {
        return new MultipleSource(iterable);
    }
}
