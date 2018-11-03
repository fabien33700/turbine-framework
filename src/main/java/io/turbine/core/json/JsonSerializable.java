package io.turbine.core.json;

import io.vertx.core.json.JsonObject;

public interface JsonSerializable {
    JsonObject toJson();
}
