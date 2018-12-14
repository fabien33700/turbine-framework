package io.turbine.core.utils.rxcollection;

import io.turbine.core.utils.rxcollection.observers.ReactiveListObserver;

import java.util.List;

/**
 * Represents the behavior of a list that can emits events when its structure change.
 * ReactiveList separate events in 3 types : additions, deletions and modifications.
 * These events are observables to allow developers to subscribe to list changes:
 * they are supplied by a list observer.
 *
 * Note that a ReactiveList is itself a ReactiveListObserver, and supply also a getObserver()
 * getter method to allow to interact with it without manipulating directly the list.
 *
 * @param <T> The type of items contained in the list
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface ReactiveList<T> extends List<T>, ReactiveListObserver<T> {
    /**
     * Returns the list event observer from the reactive list.
     * Generally, the implementation will avoid to return a self-reference and will provide
     * instead a reference to the internal observer.
     * @return A ReactiveListObserver instance
     */
    ReactiveListObserver<T> getObserver();
}