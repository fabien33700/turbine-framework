package io.turbine.core.verticles.behaviors;

import io.turbine.core.verticles.BaseVerticle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public interface Verticle {

    default <V extends BaseVerticle> V deployVerticle(Class<V> verticleClass) {
        return deployVerticle(verticleClass, new HashMap<>());
    }

    default <V extends BaseVerticle> V deployVerticle(Supplier<V> verticleSupplier) {
        return deployVerticle(verticleSupplier, new HashMap<>());
    }

    <V extends BaseVerticle> V deployVerticle(Class<V> verticleClass, Map<String, Object> parameters);

    <V extends BaseVerticle> V deployVerticle(Supplier<V> verticleSupplier, Map<String, Object> parameters);

    void inject(Map<String, Object> params);
}
