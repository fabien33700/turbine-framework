package io.turbine.core.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MultipleSource implements JsonSource {
    private final JsonArray array;

    MultipleSource(JsonArray array) {
            this.array = array;
    }

    <T> MultipleSource(Iterable<T> iterable) {
        this.array = new JsonArray();
        StreamSupport.stream(iterable.spliterator(), false)
                .map(SingleSource::new)
                .forEach(array::add);
    }

    @Override
    public String encode() {
        return array.encode();
    }

    @Override
    public String encodePrettily() {
        return array.encodePrettily();
    }

    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public <T> T mapTo(Class<T> clazz) {
        throw new UnsupportedOperationException("MultipleSource does not support single operation");
    }

    @Override
    public <T> Iterable<T> mapCollectionTo(Class<T> clazz) {
        return array.stream()
                .map(item -> ((SingleSource)item).mapTo(clazz))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return encode();
    }
}
