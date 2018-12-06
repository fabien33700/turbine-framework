package io.turbine.core.utils.rxcollection.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.turbine.core.utils.rxcollection.events.EventType.*;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableCollection;
import static java.util.Collections.unmodifiableList;

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
    public static <T> Event<T> newAdditionEvent(
            List<T> source, T item, int position)
    {
        return new EventImpl<>(source, ADDITION, item, position);
    }

    /**
     * Create an addition event.
     * @param source The source list of the event
     * @param items A list of items affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> Event<T> newAdditionEvent(
            List<T> source, Collection<? extends T> items, int position)
    {
        return new EventImpl<>(source, ADDITION, items, position);
    }

    /**
     * Create a deletion event.
     * @param source The source list of the event
     * @param item The item affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> Event<T> newDeletionEvent(
            List<T> source, T item, int position)
    {
        return new EventImpl<>(source, DELETION, item, position);
    }

    /**
     * Create a deletion event.
     * @param source The source list of the event
     * @param items A list of items affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> Event<T> newDeletionEvent(
           List<T> source, Collection<? extends T> items, int position)
    {
        return new EventImpl<>(source, DELETION, items, position);
    }

    /**
     * Create a modification event.
     * @param source The source list of the event
     * @param item The item affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> Event<T> newModificationEvent(
            List<T> source, T item, int position)
    {
        return new EventImpl<>(source, MODIFICATION, item, position);
    }

    /**
     * Create a modification event.
     * @param source The source list of the event
     * @param items A list of items affected by the event
     * @param position The position of the changed item
     * @param <T> The type of the item(s) concerned by the event
     * @return A Event implementation
     */
    public static <T> Event<T> newModificationEvent(
            List<T> source, Collection<? extends T> items, int position)
    {
        return new EventImpl<>(source, MODIFICATION, items, position);
    }

    /**
     * Standard implementation of the Event interface.
     * @param <T> The type of the item(s) concerned by the event
     */
    static final class EventImpl<T> implements Event<T> {

        private final Collection<? extends T> items;
        private final List<T> source;
        private final int position;
        private final EventType eventType;

        private final T first;

        EventImpl(List<T> source, EventType eventType,
                  final T item, int position) {
            this(source, eventType, singletonList(item), position);
        }

        EventImpl(List<T> source, EventType eventType,
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
}
