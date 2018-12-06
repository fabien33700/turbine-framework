package io.turbine.core.utils.rxcollection;

import io.reactivex.Observable;
import io.turbine.core.utils.rxcollection.events.Event;

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
     * @return A Observable of Event
     */
    Observable<Event<T>> additions();

    /**
     * An observable source of list deletions events.
     * @return A Observable of Event
     */
    Observable<Event<T>> deletions();

    /**
     * An observable source of list modifications events.
     * @return A Observable of Event
     */
    Observable<Event<T>> modifications();

}