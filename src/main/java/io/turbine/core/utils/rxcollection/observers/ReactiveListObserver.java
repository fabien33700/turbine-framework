package io.turbine.core.utils.rxcollection.observers;

import io.reactivex.Observable;
import io.turbine.core.utils.rxcollection.events.ListEvent;

import java.util.List;

/**
 * The standard behavior of a reactive list observer, which means
 * an object that must provide the Observable sources of list changes events.
 * @param <T> The type of items contained in the list
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface ReactiveListObserver<T> {

    /**
     * An observable source of list additions events.
     * @return A Observable of ListEvent
     */
    Observable<ListEvent<T>> additions();

    /**
     * An observable source of list deletions events.
     * @return A Observable of ListEvent
     */
    Observable<ListEvent<T>> deletions();

    /**
     * An observable source of list modifications events.
     * @return A Observable of ListEvent
     */
    Observable<ListEvent<T>> modifications();

    /**
     * Returns a readonly copy of the source list.
     * @return A List instance
     */
    List<T> readList();
}