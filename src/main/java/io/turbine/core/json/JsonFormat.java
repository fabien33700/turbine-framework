package io.turbine.core.json;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static io.turbine.core.utils.Utils.Dates.formatDateIso3601;
import static io.turbine.core.utils.Utils.Reflection.isPrimitiveOrWrapper;
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

}
