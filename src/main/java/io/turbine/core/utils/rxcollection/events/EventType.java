package io.turbine.core.utils.rxcollection.events;

/**
 * An enum that indicates the event type.
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public enum EventType {
    /**
     * Represents an item addition. A such typed event is triggered by
     * List interface methods add() and addAll().
     */
    ADDITION,

    /**
     * Represents an item deletion. A such typed event is triggered by
     * List interface methods remove(), removeAll(), retainAll(), clear().
     */
    DELETION,

    /**
     * Represents an item modification. A such typed event is triggered by
     * List interface methods set().
     */
    MODIFICATION
}
