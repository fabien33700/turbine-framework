package io.turbine.core.utils.rxcollection.events;

import java.util.List;

/**
 * The standard structure of a list change event.
 * @param <T> The type of list item concerned by the event
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface Event<T> {
    /**
     * A sequence of the items affected by a previous operation.
     * @return A Iterable instance of T items
     */
    Iterable<T> items();

    /**
     * If available, the position at which item(s) had been inserted/removed/modified.
     * Position will not be available for List operations that can potentially affect items
     * in a discontinuous way, such as removeAll(), retainsAll() or for operation for which
     * the position information is not pertinent such as clear().
     * @return The position of first inserted/deleted/modified item
     */
    int position();

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
    List<T> source();

    /**
     * The type of the event.
     * @return A EventType enum value
     */
    EventType eventType();

    /**
     * An utility method to get the first affected item.
     * @return The first affected T item
     */
    T first();
}
