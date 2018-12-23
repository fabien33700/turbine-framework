package io.turbine.core.json;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.turbine.core.utils.Utils.Dates.formatDateIso3601;
import static io.turbine.core.utils.Utils.Reflection.isPrimitiveOrWrapper;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collector.of;

public final class JsonFormat {

    private static final Collector<Object, ?, JsonArray> JSON_ARRAY_COLLECTOR = of(
            JsonArray::new, JsonArray::add, JsonArray::addAll);

    public static Collector<Object, ?, JsonArray> jsonArrayCollector() {
        return JSON_ARRAY_COLLECTOR;
    }

    private static JsonObject parseJsonObject(Object response) {
        final JsonObject json;
        if (response instanceof JsonSerializable) {
            return  ((JsonSerializable) response).toJson();
        } else if (response instanceof JsonObject) {
            return (JsonObject) response;
        } else {
            return new JsonObject(Json.encode(response));
        }
    }

    @SuppressWarnings("unchecked")
    public static String printJson(Object response) {
        /* For lists, call parseJsonObject on each item */
        if (response instanceof List) {
            List<Object> list = (List<Object>) response;
            JsonArray array = list.stream()
                    .map(JsonFormat::parseJsonObject)
                    .collect(jsonArrayCollector());

            return array.encodePrettily();
        /* For Map, convert Map<?,?> to Map<String, Object>
            and use JsonObject(Map) constructor */
        } else if (response instanceof Map) {
            Map<String, Object> map = new LinkedHashMap<>();
            ((Map) response).forEach((k, v) -> map.put(k.toString(), v));

            return new JsonObject(map).encodePrettily();

        /* Return strings surrounded with double-quote */
        } else if (response instanceof String) {
            return "\"" + response + "\"";

        /* Returns Java 8 Time ISO-8601 string representation */
        } else if (response instanceof Instant) {
            return "\"" + formatDateIso3601((Instant) response) + "\"";

        /* Returns standard toString() representation for primitive values (wrapper) */
        } else if (isPrimitiveOrWrapper(response)) {
            return response.toString();

        } else {
            return parseJsonObject(response).encodePrettily();
        }
    }

    public static JsonArray createJsonArray(Object... objects) {
        requireNonNull(objects, "objects");
        return Stream.of(objects).collect(JSON_ARRAY_COLLECTOR);
    }

    @SuppressWarnings("unchecked")
    public static JsonObject fromString(String json) {
        Map<String, Object> map = (Map<String, Object>) Json.decodeValue(json, Map.class);
        return new JsonObject(map);
    }

    public static final class Builder {

        public static Builder create() {
            return new Builder();
        }

        public static Builder from(JsonObject json) {
            return new Builder(json);
        }

        private final JsonObject json;

        private Builder() {
            json = new JsonObject();
        }

        private Builder(JsonObject json) {
            this.json = json;
        }

        public <K, V> Builder put(K key, V value) {
            json.put(key.toString(), value);
            return this;
        }

        public <K, V> Builder put(Iterable<K> keys, Iterable<V> values) {
            Iterator<K> itKeys = keys.iterator();
            Iterator<V> itValues = values.iterator();

            while (itKeys.hasNext() && itValues.hasNext())
                json.put(itKeys.next().toString(), itValues.next().toString());

            return this;
        }

        public <K, V> Builder put(Iterable<K> keys, Function<K, V> valueFn) {
            StreamSupport.stream(keys.spliterator(), false)
                .forEachOrdered(key -> json.put(key.toString(), valueFn.apply(key).toString()));

            return this;
        }

        public <K, V> Builder put(Map<K, V> sourceMap) {
            sourceMap.forEach((k, v) -> json.put(k.toString(), v.toString()));
            return this;
        }

        public <K, V> Builder put(K[] keys, V[] values) {
            return put(asList(keys), asList(values));
        }

        public <K, V> Builder put(K[] keys, Function<K, V> valueFn) {
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

    }
}
