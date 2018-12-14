package io.turbine.core.utils.rxcollection;

import io.turbine.core.utils.rxcollection.observers.ReactiveMapObserver;

import java.util.Map;

public interface ReactiveMap<K, V> extends Map<K, V>, ReactiveMapObserver<K, V> {
    ReactiveMapObserver<K, V> getObserver();
}
