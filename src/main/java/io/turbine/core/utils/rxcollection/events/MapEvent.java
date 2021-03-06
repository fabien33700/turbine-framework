package io.turbine.core.utils.rxcollection.events;

import java.util.Map;

/**
 * The standard structure of a list change event.
 * @param <K> The type of map key concerned by the event
 * @param <V> The type of map value concerned by the vent
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface MapEvent<K, V> {
    /**
     * A sequence of the keys affected by a previous operation.
     * @return A Iterable instance of K keys
     */
    Iterable<K> keys();

    /**
     * A sequence of the values affected by a previous operation.
     * @return A Iterable instance of V values
     */
    Iterable<V> values();

    /**
     * A sequence of the map entries affected by a previous operation.
     * @return A Iterable instance of K, V Map's entry
     */
    Iterable<Map.Entry<? extends K, ? extends V>> entries();

    /**
     * Gives the number of affected items.
     * In most cases, it is equivalent with Event#items().size().
     * @return The affected items count
     */
    int affected();

    /**
     * A unmodifiable reference of the event's source list.
     * @return A unmodifiable list
     */
    Map<K, V> source();

    /**
     * The type of the event.
     * @return A EventType enum value
     */
    EventType eventType();

    /**
     * An utility method to get the first affected key.
     * @return The first affected K key
     */
    K firstKey();

    /**
     * An utility method to get the first affected value.
     * @return The first affected V value
     */
    V firstValue();
}
