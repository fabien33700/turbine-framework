package io.turbine.core.jdbc.transformers;


import io.turbine.core.jdbc.ResultTransformer;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public class ModelTransformer<M> implements ResultTransformer<M> {

    interface Property<M> {
        Function<M, Object> getter();
        BiConsumer<M, Object> setter();
    }

    class PropertyImpl<M> implements Property<M> {
        private final Function<M, Object> getter;
        private final BiConsumer<M, Object> setter;

        PropertyImpl(Function<M, Object> getter, BiConsumer<M, Object> setter) {
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public BiConsumer<M, Object> setter() {
            return setter;
        }

        @Override
        public Function<M, Object> getter() {
            return getter;
        }
    }

    private final Map<String, Property<M>> bindings;

    private final Supplier<M> factory;

    public ModelTransformer(Supplier<M> factory) {
        requireNonNull(factory, "You must provide a non-null factory. Try use MyClass::new");
        this.bindings = new HashMap<>();
        this.factory = factory;
    }

    public ModelTransformer<M> bind(String propertyName,
                                    Function<M, Object> getter,
                                    BiConsumer<M, Object> setter)
    {
        requireNonNull(getter, "The property getter could not be null");
        requireNonNull(setter, "The property setter could not be null");

        if (propertyName == null || propertyName.trim().isEmpty()) {
            throw new IllegalArgumentException("The property name could not be null nor empty.");
        }

        internalBind(propertyName, getter, setter);
        return this;
    }

    private void internalBind(String propertyName,
                              Function<M, Object> getter,
                              BiConsumer<M, Object> setter)
    {
        Property<M> property = new PropertyImpl<>(getter, setter);
        bindings.put(propertyName, property);
    }

    @Override
    public M apply(JsonObject json) throws Exception {
        M model = factory.get();
        Set<String> keys = json.getMap().keySet();

        bindings.forEach((key, prop) -> {
            if (!keys.contains(key)) return;

            prop.setter().accept(model, json.getMap().get(key));
        });
        return model;
    }
}
