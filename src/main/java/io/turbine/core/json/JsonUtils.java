package io.turbine.core.json;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collector;

import static java.util.stream.Collector.of;

public class JsonUtils {

    private static Collector<Object, ?, JsonArray> JSON_ARRAY_COLLECTOR = of(
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
        if (response instanceof List) {
            List<Object> list = (List<Object>) response;
            JsonArray array = list.stream()
                    .map(JsonUtils::parseJsonObject)
                    .collect(jsonArrayCollector());

            return array.encodePrettily();
        }
        return parseJsonObject(response).encodePrettily();
    }
}
