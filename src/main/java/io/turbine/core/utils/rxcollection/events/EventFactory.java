package io.turbine.core.utils.rxcollection.events;

import java.util.*;

import static io.turbine.core.utils.rxcollection.events.EventType.*;
import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * A factory for creating list changes event implementation.
 * Note that the standard Event implementation is internal to the factory,
 * to prevent external instanciation of it.
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class EventFactory {

    /**
     * Create an addition event.
     * @param source The source list of the event
     * @param item The item affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> ListEvent<T> newAdditionListEvent(
            List<T> source, T item, int position)
    {
        return new ListEventImpl<>(source, ADDITION, item, position);
    }

    /**
     * Create an addition event for a List.
     * @param source The source list of the event
     * @param items A list of items affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> ListEvent<T> newAdditionListEvent(
            List<T> source, Collection<? extends T> items, int position)
    {
        return new ListEventImpl<>(source, ADDITION, items, position);
    }

    /**
     * Create a deletion event for a List.
     * @param source The source list of the event
     * @param item The item affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> ListEvent<T> newDeletionListEvent(
            List<T> source, T item, int position)
    {
        return new ListEventImpl<>(source, DELETION, item, position);
    }

    /**
     * Create a deletion event for a List.
     * @param source The source list of the event
     * @param items A list of items affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> ListEvent<T> newDeletionListEvent(
           List<T> source, Collection<? extends T> items, int position)
    {
        return new ListEventImpl<>(source, DELETION, items, position);
    }

    /**
     * Create a modification event for a List.
     * @param source The source list of the event
     * @param item The item affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> ListEvent<T> newModificationListEvent(
            List<T> source, T item, int position)
    {
        return new ListEventImpl<>(source, MODIFICATION, item, position);
    }

    /**
     * Create a modification event for a List.
     * @param source The source list of the event
     * @param items A list of items affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> ListEvent<T> newModificationListEvent(
            List<T> source, Collection<? extends T> items, int position)
    {
        return new ListEventImpl<>(source, MODIFICATION, items, position);
    }

    /**
     * Standard implementation of the Event interface.
     * @param <T> The type of the item(s) concerned by the event
     */
    static final class ListEventImpl<T> implements ListEvent<T> {

        private final Collection<? extends T> items;
        private final List<T> source;
        private final int position;
        private final EventType eventType;

        private final T first;

        ListEventImpl(List<T> source, EventType eventType,
                      final T item, int position) {
            this(source, eventType, singletonList(item), position);
        }

        ListEventImpl(List<T> source, EventType eventType,
                      final Collection<? extends T> items, int position) {
            this.source = unmodifiableList(source);
            this.eventType = eventType;
            this.items = new ArrayList<>(items);
            this.position = position;
            this.first = items.iterator().next();
        }

        @Override
        public Iterable<T> items() {
            return unmodifiableCollection(items);
        }

        @Override
        public List<T> source() {
            return source;
        }

        @Override
        public int affected() {
            return items.size();
        }

        @Override
        public int position() {
            return position;
        }

        @Override
        public EventType eventType() {
            return eventType;
        }

        @Override
        public T first() {
            return first;
        }
    }

    /**
     * Create an addition event for a Map.
     * @param source The source map of the event
     * @param entries The entries affected by the event
     * @param <K> The type of the keys concerned by the event
     * @param <V> The type of the values concerned by the event
     * @return A Event implementation
     */
    public static <K, V> MapEvent<K, V> newAdditionMapEvent(
            Map<K, V> source, Map<? extends K, ? extends V> entries)
    {
        return new MapEventImpl<>(source, ADDITION, entries);
    }

    /**
     * Create an addition event for a Map.
     * @param source The source map of the event
     * @param key The entry key affected by the event
     * @param value The entry value affected by the event
     * @param <K> The type of the keys concerned by the event
     * @param <V> The type of the values concerned by the event
     * @return A Event implementation
     */
    public static <K, V> MapEvent<K, V> newAdditionMapEvent(
            Map<K, V> source, K key, V value)
    {
        return new MapEventImpl<>(source, ADDITION, singletonMap(key, value));
    }

    /**
     * Create a deletion event for a Map.
     * @param source The source map of the event
     * @param entries The entries affected by the event
     * @param <K> The type of the keys concerned by the event
     * @param <V> The type of the values concerned by the event
     * @return A Event implementation
     */
    public static <K, V> MapEvent<K, V> newDeletionMapEvent(
            Map<K, V> source, Map<K, V> entries)
    {
        return new MapEventImpl<>(source, DELETION, entries);
    }

    /**
     * Create a deletion event for a Map.
     * @param source The source map of the event
     * @param key The entry key affected by the event
     * @param value The entry value affected by the event
     * @param <K> The type of the keys concerned by the event
     * @param <V> The type of the values concerned by the event
     * @return A Event implementation
     */
    public static <K, V> MapEvent<K, V> newDeletionMapEvent(
            Map<K, V> source, K key, V value)
    {
        return new MapEventImpl<>(source, DELETION, singletonMap(key, value));
    }

    /**
     * Create a modification event for a Map.
     * @param source The source map of the event
     * @param entries The entries affected by the event
     * @param <K> The type of the keys concerned by the event
     * @param <V> The type of the values concerned by the event
     * @return A Event implementation
     */
    public static <K, V> MapEvent<K, V> newModificationMapEvent(
            Map<K, V> source, Map<K, V> entries)
    {
        return new MapEventImpl<>(source, MODIFICATION, entries);
    }

    /**
     * Create a modification event for a Map.
     * @param source The source map of the event
     * @param key The entry key affected by the event
     * @param value The entry value affected by the event
     * @param <K> The type of the keys concerned by the event
     * @param <V> The type of the values concerned by the event
     * @return A Event implementation
     */
    public static <K, V> MapEvent<K, V> newModificationMapEvent(
            Map<K, V> source, K key, V value)
    {
        return new MapEventImpl<>(source, MODIFICATION, key, value);
    }

    static final class MapEventImpl<K, V> implements MapEvent<K, V> {

        private final Set<Map.Entry<? extends K, ? extends V>> entries;
        private final Map<K, V> source;
        private final EventType eventType;
        private final Map.Entry<? extends K, ? extends V> first;

        public MapEventImpl(Map<K, V> source, EventType eventType, K key, V value) {
            this(source, eventType, singletonMap(key, value));
        }

        public MapEventImpl(Map<K, V> source, EventType eventType, Map<? extends K, ? extends V> entries) {
            this.entries = new HashSet<>(entries.entrySet());
            this.source = source;
            this.eventType = eventType;
            this.first = this.entries.iterator().next();
        }

        @Override
        public Iterable<K> keys() {
            return entries.stream()
                    .map(Map.Entry::getKey)
                    .collect(toSet());
        }

        @Override
        public Iterable<V> values() {
            return entries.stream()
                    .map(Map.Entry::getValue)
                    .collect(toList());
        }

        @Override
        public Iterable<Map.Entry<? extends K, ? extends V>> entries() {
            return entries;
        }

        @Override
        public int affected() {
            return entries.size();
        }

        @Override
        public Map<K, V> source() {
            return unmodifiableMap(source);
        }

        @Override
        public EventType eventType() {
            return eventType;
        }

        @Override
        public K firstKey() {
            return first.getKey();
        }

        @Override
        public V firstValue() {
            return first.getValue();
        }
    }
}
