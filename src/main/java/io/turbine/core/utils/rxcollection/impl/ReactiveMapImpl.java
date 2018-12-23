package io.turbine.core.utils.rxcollection.impl;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import io.turbine.core.utils.rxcollection.ReactiveMap;
import io.turbine.core.utils.rxcollection.events.EventType;
import io.turbine.core.utils.rxcollection.events.MapEvent;
import io.turbine.core.utils.rxcollection.observers.ReactiveMapObserver;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static io.turbine.core.utils.rxcollection.events.EventFactory.*;

public class ReactiveMapImpl<K, V> implements ReactiveMap<K, V> {

    /**
     * A subject for emitting all events
     */
    private final Subject<MapEvent<K, V>> events = BehaviorSubject.create();

    @Override
    public Observable<MapEvent<K, V>> additions() {
        return observer.additions();
    }

    @Override
    public Observable<MapEvent<K, V>> deletions() {
        return observer.deletions();
    }

    @Override
    public Observable<MapEvent<K, V>> modifications() {
        return observer.modifications();
    }

    /**
     * A standard ReactiveListObserver internal implementation, that uses the
     * global events source and partition it according to the EventType.
     */
    final class ReactiveMapObserverImpl implements ReactiveMapObserver<K, V> {

        @Override
        public Observable<MapEvent<K, V>> additions() {
            return events.filter(e -> e.eventType() == EventType.ADDITION);
        }

        @Override
        public Observable<MapEvent<K, V>> deletions() {
            return events.filter(e -> e.eventType() == EventType.DELETION);
        }

        @Override
        public Observable<MapEvent<K, V>> modifications() {
            return events.filter(e -> e.eventType() == EventType.MODIFICATION);
        }
    }

    /**
     * The list observer reference
     */
    private final ReactiveMapObserver<K, V> observer = new ReactiveMapObserverImpl();

    /**
     * The underlying delegated list instance
     */
    private final Map<K, V> delegate;

    /**
     * Create an empty reactive list.
     */
    public ReactiveMapImpl() {
        delegate = new HashMap<>();
    }

    /**
     * Create an empty reactive map, using the given factory to instanciate
     * the specific type of source map.
     * @param mapFactory A map factory
     */
    public ReactiveMapImpl(Supplier<Map<K, V>> mapFactory) {
        delegate = mapFactory.get();
    }

    /**
     * Create a reactive map on the given list.
     * Given that the given map will be used as a delegate,
     * all changes applied to the reactive map will be passed on it, and reciprocally, but
     * if a developer uses methods to affect map using the source map reference, changes
     * will not be captured and emitted by the observer.
     * @param delegate The list to wrap
     */
    public ReactiveMapImpl(Map<K, V> delegate) {
        this.delegate = delegate;
    }


    @Override
    public ReactiveMapObserver<K, V> getObserver() {
        return observer;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegate.get(key);
    }

    @Override
    public V put(K key, V value) {
        V previous = delegate.put(key, value);
        if (previous == null) {
            events.onNext(newAdditionMapEvent(this, key, value));
        } else {
            events.onNext(newModificationMapEvent(this, key, value));
        }

        return previous;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object key) {
        V previous = delegate.remove(key);
        if (previous != null) {
            events.onNext(newDeletionMapEvent(this, (K) key, previous));
        }
        return previous;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
        events.onNext(newAdditionMapEvent(this, m));
    }

    @Override
    public void clear() {
        events.onNext(newDeletionMapEvent(this, this));
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Map<K, V> updates = new HashMap<>();
        for (Entry<K, V> entry : entrySet()) {
            K key;
            V actual, updated;
            try {
                key = entry.getKey();
                actual = entry.getValue();
            } catch (IllegalStateException ex) {
                throw new ConcurrentModificationException();
            }
            updated = function.apply(key, actual);
            if (updated != actual) {
                updates.put(key, updated);
            }
            try {
                entry.setValue(updated);
            } catch (IllegalStateException ex) {
                throw new ConcurrentModificationException();
            }
        }
        if (!updates.isEmpty()) {
            events.onNext(newModificationMapEvent(this, updates));
        }
    }
}
