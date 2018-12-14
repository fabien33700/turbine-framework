package io.turbine.core.deployment;

import io.turbine.core.verticles.behaviors.Verticle;

import java.util.function.Supplier;

import static io.turbine.core.utils.Utils.Reflection.getGenericTypeOf;
import static java.util.Objects.requireNonNull;

public interface VerticleFactory<V extends Verticle> {
    String verticleName();
    V get();

    static <V extends Verticle> VerticleFactory<V>
    fromClass(Class<V> verticleClass) {
        requireNonNull(verticleClass, "verticle class");
        if (!Verticle.class.isAssignableFrom(verticleClass)) {
            throw new IllegalArgumentException("Verticle class must be inherited from Verticle");
        }
        return new VerticleFactory<V>() {
            @Override
            public String verticleName() {
                return verticleClass.getName();
            }

            @Override
            public V get() {
                try {
                    return verticleClass.newInstance();
                } catch (Exception ex) {
                    throw new RuntimeException("Cannot create a verticle from class "
                            + verticleName(), ex);
                }
            }
        };
    }

    static <V extends Verticle>
    VerticleFactory<V> fromSupplier(Supplier<V> verticleSupplier) {
        requireNonNull(verticleSupplier, "verticle supplier function");
        return new VerticleFactory<V>() {
            @Override
            public String verticleName() {
                return getGenericTypeOf(verticleSupplier.getClass()).getName();
            }

            @Override
            public V get() {
                return verticleSupplier.get();
            }
        };
    }

}
