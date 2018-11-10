package io.turbine.core.json;

import io.vertx.core.json.JsonObject;

/**
 * Defines the behavior of an object that can serialized itself to JSON object.
 */
public interface JsonSerializable {
    /**
     * Returns a JsonObject representation of itself
     * @return A JsonObject instance
     */
    JsonObject toJson();

    /**
     * Returns a Json representation of itself as string
     * @return A Json string
     */
    default String toJsonString() {
        return toJson().encodePrettily();
    }
}
