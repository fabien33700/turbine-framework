package io.turbine.core.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collector.of;

public interface JsonSource {

    Collector<JsonObject, ?, JsonArray> JSON_ARRAY_COLLECTOR = of(
            JsonArray::new, JsonArray::add, JsonArray::addAll);

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

    static JsonSource from(Object... objects) {
        return from(asList(objects));
    }

    static JsonSource from(final JsonArray array) {
        return new MultipleSource(array);
    }

    static <T> JsonSource from(Iterable<T> iterable) {
        return new MultipleSource(iterable);
    }
}
