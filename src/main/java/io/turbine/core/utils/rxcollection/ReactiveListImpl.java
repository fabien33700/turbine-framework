package io.turbine.core.utils.rxcollection;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import io.turbine.core.utils.rxcollection.events.Event;
import io.turbine.core.utils.rxcollection.events.EventType;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static io.turbine.core.utils.rxcollection.events.EventFactory.*;
import static java.util.Collections.unmodifiableList;

/**
 * A standard implementation of ReactiveList that uses delegation to wraps a classic
 * List implementation and capture the modification events, emitting them with the observer.
 * @param <T> The type of items contained in the list
 */
public class ReactiveListImpl<T> implements ReactiveList<T> {

    /**
     * A subject for emitting all events
     */
    private final Subject<Event<T>> events = BehaviorSubject.create();

    /**
     * A standard ReactiveListObserver internal implementation, that uses the
     * global events source and partition it according to the EventType.
     */
    final class ReactiveListObserverImpl implements ReactiveListObserver<T> {
        
        @Override
        public Observable<Event<T>> additions() {
            return events.filter(e -> e.eventType() == EventType.ADDITION);
        }

        @Override
        public Observable<Event<T>> deletions() {
            return events.filter(e -> e.eventType() == EventType.DELETION);
        }

        @Override
        public Observable<Event<T>> modifications() {
            return events.filter(e -> e.eventType() == EventType.MODIFICATION);
        }
    }

    /**
     * The list observer reference
     */
    private final ReactiveListObserver<T> observer = new ReactiveListObserverImpl();

    /**
     * The underlying delegated list instance
     */
    private final List<T> delegate;

    /**
     * Create an empty reactive list.
     */
    public ReactiveListImpl() {
        delegate = new ArrayList<>();
    }

    /**
     * Create an empty reactive list, using the given factory to instanciate
     * the specific type of source list.
     * @param listFactory A list factory
     */
    public ReactiveListImpl(Supplier<List<T>> listFactory) {
        delegate = listFactory.get();
    }

    /**
     * Create a reactive list on the given list.
     * Given that the given list will be used as a delegate,
     * all changes applied to the reactive list will be passed on it, and reciprocally, but
     * if a developer uses methods to affect list using the source list reference, changes
     * will not be captured and emitted by the observer.
     * @param delegate The list to wrap
     */
    public ReactiveListImpl(List<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Observable<Event<T>> additions() {
        return observer.additions();
    }

    @Override
    public Observable<Event<T>> deletions() {
        return observer.deletions();
    }

    @Override
    public Observable<Event<T>> modifications() {
        return observer.modifications();
    }

    public List<T> getDelegate() {
        return unmodifiableList(delegate);
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
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(T t) {
        boolean success = delegate.add(t);
        if (success)
            events.onNext(newAdditionEvent(this, t, size()));

        return success;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        T t = (T) o;
        int pos = delegate.indexOf(t);
        boolean success = delegate.remove(o);
        if (success)
            events.onNext(newDeletionEvent(this, t, pos));
        return success;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean success = delegate.addAll(c);
        if (success)
            events.onNext(newAdditionEvent(this, c, size()));
        return success;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean success = delegate.addAll(index, c);
        if (success)
            events.onNext(newAdditionEvent(this, c, index));
        return success;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean removeAll(Collection<?> c) {
        Collection<T> ct = (Collection<T>) c;
        boolean success = delegate.removeAll(c);
        if (success)
            events.onNext(newDeletionEvent(this, ct, -1));
        return success;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean retainAll(Collection<?> c) {
        Collection<T> excluded = delegate.stream()
                .filter(t -> !c.contains(t))
                .collect(Collectors.toList());
        boolean success = delegate.retainAll(c);
        if (success)
            events.onNext(newDeletionEvent(this, excluded, -1));
        return success;
    }

    @Override
    public void clear() {
        List<T> deleted = unmodifiableList(new ArrayList<>(delegate));
        events.onNext(newDeletionEvent(this, deleted, -1));
        delegate.clear();
    }

    @Override
    public T get(int index) {
        return delegate.get(index);
    }

    @Override
    public T set(int index, T element) {
        events.onNext(newModificationEvent(this, element, index));
        return delegate.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        delegate.add(index, element);
        events.onNext(newAdditionEvent(this, element, index));
    }

    @Override
    public T remove(int index) {
        T deleted = delegate.remove(index);
        events.onNext(newDeletionEvent(this, deleted, index));
        return deleted;
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new ReactiveListImpl<>(delegate.subList(fromIndex, toIndex));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return obj instanceof ReactiveListImpl &&
                listEquals(((ReactiveListImpl) obj).getDelegate(), delegate);
    }

    /**
     * Indicates whether the given lists are equals, in the same order.
     * @param first The first list
     * @param second The second list
     * @param <T> The type of items contained in the lists
     * @return true if both lists are equals, false otherwise.
     */
    private static <T> boolean listEquals(List<T> first, List<T> second) {
        Iterator<T> itFirst = first.iterator();
        Iterator<T> itSecond = second.iterator();
        while (itFirst.hasNext() && itSecond.hasNext()) {
            if (!itFirst.next().equals(itSecond.next())) {
                return false;
            }
        }

        return !itFirst.hasNext() && !itSecond.hasNext();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public ReactiveListObserver<T> getObserver() {
        return observer;
    }
}
