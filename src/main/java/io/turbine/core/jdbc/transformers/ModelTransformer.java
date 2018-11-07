package io.turbine.core.jdbc.transformers;


import io.turbine.core.json.JsonSerializable;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class ModelTransformer<M extends JsonSerializable> implements ResultTransformer<M> {

    public interface Setter<M> extends BiConsumer<M, Object> {}

    private final Map<String, Setter<M>> bindings;

    private final Supplier<M> factory;

    public ModelTransformer(Supplier<M> factory) {
        requireNonNull(factory, "You must provide a non-null factory. Try use MyClass::new");
        this.bindings = new HashMap<>();
        this.factory = factory;
    }

    public ModelTransformer<M> bind(String propertyName,
                                    Setter<M> setter)
    {
        requireNonNull(setter, "The property setter could not be null");

        if (propertyName == null || propertyName.trim().isEmpty()) {
            throw new IllegalArgumentException("The property name could not be null nor empty.");
        }

        bindings.put(propertyName, setter);
        return this;
    }

    @Override
    public M apply(JsonObject json) throws Exception {
        M model = factory.get();
        Set<String> keys = json.getMap().keySet();

        bindings.forEach((key, setter) -> {
            if (!keys.contains(key)) return;
            setter.accept(model, json.getMap().get(key));
        });
        return model;
    }
}
