package io.turbine.core.json;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

public final class JsonBuilder {

    public static JsonBuilder create() {
        return new JsonBuilder();
    }

    public static JsonBuilder from(JsonObject json) {
        return new JsonBuilder(json);
    }

    private final JsonObject json;

    private JsonBuilder() {
        json = new JsonObject();
    }

    private JsonBuilder(JsonObject json) {
        this.json = json;
    }

    public <K, V> JsonBuilder put(K key, V value) {
        json.put(key.toString(), value);
        return this;
    }

    public <K, V> JsonBuilder put(Iterable<K> keys, Iterable<V> values) {
        Iterator<K> itKeys = keys.iterator();
        Iterator<V> itValues = values.iterator();

        while (itKeys.hasNext() && itValues.hasNext())
            json.put(itKeys.next().toString(), itValues.next().toString());

        return this;
    }

    public <K, V> JsonBuilder put(Iterable<K> keys, Function<K, V> valueFn) {
        StreamSupport.stream(keys.spliterator(), false)
            .forEachOrdered(key -> json.put(key.toString(), valueFn.apply(key).toString()));

        return this;
    }

    public <K, V> JsonBuilder put(Map<K, V> sourceMap) {
        sourceMap.forEach((k, v) -> json.put(k.toString(), v.toString()));
        return this;
    }

    public <K, V> JsonBuilder put(K[] keys, V[] values) {
        return put(asList(keys), asList(values));
    }

    public <K, V> JsonBuilder put(K[] keys, Function<K, V> valueFn) {
        return put(asList(keys), valueFn);
    }

    public JsonObject build() {
        return json;
    }

    public static <K, V> JsonObject json(K key, V value) {
        return create()
            .put(key.toString(), value)
            .build();
    }

    public static <K, V> JsonObject json(Iterable<K> keys, Iterable<V> values) {
        return create()
            .put(keys, values)
            .build();
    }

    public static <K, V> JsonObject json(Iterable<K> keys, Function<K, V> valueFn) {
        return create()
            .put(keys, valueFn)
            .build();
    }

    public static <K, V> JsonObject json(Map<K, V> sourceMap) {
        return create()
            .put(sourceMap)
            .build();
    }

    public static <K, V> JsonObject json(K[] keys, V[] values) {
        return create()
            .put(keys, values)
            .build();
    }

    public static <K, V> JsonObject json(K[] keys, Function<K, V> valueFn) {
        return create()
            .put(keys, valueFn)
            .build();
    }

    @SuppressWarnings("unchecked")
    public static JsonObject fromString(String json) {
        Map<String, Object> map = (Map<String, Object>) Json.decodeValue(json, Map.class);
        return new JsonObject(map);
    }

}
