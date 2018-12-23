package io.turbine.core.utils;

import java.util.Optional;

/**
 * Describes an composite object, which means an object that can contains itself
 * object of same type of it.
 * This interface implements the design patten Composite in order to
 * modelize tree in an object graph.
 *
 * @param <T> The type of the object, must be itself a Composite
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface Composite<T extends Composite<T>> {

    /**
     * The list of object children.
     * @return Iterable of children
     */
    Iterable<T> getChildren();

    /**
     * The parent, if defined.
     * @return Optional of parent
     */
    Optional<T> getParent();

    /**
     * Set the parent of the object
     * @param parent The parent object to set
     */
    void setParent(T parent);

    /**
     * Indicates whether the object has a parent.
     * @return true if a parent is defined, false otherwise
     */
    default boolean hasParent() {
        return getParent().isPresent();
    }
}
