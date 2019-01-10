package io.turbine.core.utils.rxcollection.observers;

import io.reactivex.Observable;
import io.turbine.core.utils.rxcollection.events.MapEvent;

import java.util.Map;

/**
 * The standard behavior of a reactive map observer, which means
 * an object that must provide the Observable sources of map changes events.
 * @param <K> The type of keys contained in the map
 * @param <V> The type of values contained in the map
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
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

    /**
     * Returns a readonly copy of the source map.
     * @return A Map instance
     */
    Map<K, V> readMap();
}
