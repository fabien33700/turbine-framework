package io.turbine.core.utils.rxcollection;

import io.reactivex.Observable;
import io.turbine.core.utils.rxcollection.events.ListEvent;
import io.turbine.core.utils.rxcollection.events.MapEvent;

public interface ReactiveMapObserver<K, V> {

    /**
     * An observable source of map additions events.
     * @return A Observable of MapEvent
     */
    Observable<MapEvent<K, V>> additions();

    /**
     * An observable source of map deletions events.
     * @return A Observable of MapEvent
     */
    Observable<MapEvent<K, V>>deletions();

    /**
     * An observable source of map modifications events.
     * @return A Observable of MapEvent
     */
    Observable<MapEvent<K, V>> modifications();
}
