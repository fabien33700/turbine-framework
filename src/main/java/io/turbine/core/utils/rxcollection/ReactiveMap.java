package io.turbine.core.utils.rxcollection;

import io.turbine.core.utils.rxcollection.observers.ReactiveMapObserver;

import java.util.Map;

/**
 * Represents the behavior of a map that can emits events when its structure change.
 * ReactiveMap separate events in 3 types : additions, deletions and modifications.
 * These events are observables to allow developers to subscribe to map changes:
 * they are supplied by a map observer.
 *
 * Note that a ReactiveMap is itself a ReactiveMapObserver, and supplies also a getObserver()
 * getter method to allow to interact with it without manipulating directly the list.
 *
 * @param <K> The type of keys contained in the map
 * @param <V> The type of values contained in the map
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface ReactiveMap<K, V> extends Map<K, V>, ReactiveMapObserver<K, V> {
    /**
     * Returns the map event observer from the reactive map.
     * Generally, the implementation will avoid to return a self-reference and will provide
     * instead a reference to the internal observer.
     * @return A ReactiveMapObserver instance
     */
    ReactiveMapObserver<K, V> getObserver();
}
