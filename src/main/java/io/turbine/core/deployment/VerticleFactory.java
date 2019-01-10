package io.turbine.core.deployment;

import io.turbine.core.verticles.behaviors.Verticle;

/**
 * A function to return a verticle instance or to throw an exception
 * in case of failure.
 * @param <V> The type of Verticle to create
 */
public interface VerticleFactory<V extends Verticle> extends Factory<V> {
    Class<V> verticleClass();

    static <V extends Verticle> VerticleFactory<V> factory(Factory<V> factory, Class<V> verticleClass) {
        return new VerticleFactory<V>() {
            @Override
            public V create() throws Exception {
                return factory.create();
            }

            @Override
            public Class<V> verticleClass() {
                return verticleClass;
            }
        };
    }

    static <V extends Verticle> VerticleFactory<V> factory(Class<V> verticleClass) {
        return factory(verticleClass::newInstance, verticleClass);
    }
}
