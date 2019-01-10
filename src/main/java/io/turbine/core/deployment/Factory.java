package io.turbine.core.deployment;

public interface Factory<T> {
    T create() throws Exception;
}
