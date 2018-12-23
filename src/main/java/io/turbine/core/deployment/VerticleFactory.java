package io.turbine.core.deployment;

import io.turbine.core.verticles.behaviors.Verticle;

/**
 * A function to return a verticle instance or to throw an exception
 * in case of failure.
 * @param <V> The type of Verticle to create
 */
public interface VerticleFactory<V extends Verticle>  {
    V create() throws Exception;
}
