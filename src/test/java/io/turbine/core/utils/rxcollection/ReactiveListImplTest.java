package io.turbine.core.utils.rxcollection;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.turbine.core.utils.rxcollection.events.ListEvent;
import io.turbine.core.utils.rxcollection.impl.ReactiveListImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.turbine.core.utils.rxcollection.events.EventType.*;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class ReactiveListImplTest {

    static final List<String> ELEMENTS = asList(
            "Sansa", "Robb", "Arya", "Jon", "Rickon", "Bran", "Ned"
    );

    private ReactiveList<String> list;

    private List<Disposable> subscriptions;

    private void register(Disposable disposable) {
        subscriptions.add(disposable);
    }

    @BeforeEach
    void setUp() {
        subscriptions = new ArrayList<>();
        list = new ReactiveListImpl<>(
                new ArrayList<>(ELEMENTS));
    }

    @AfterEach
    void tearDown() {
        subscriptions.forEach(Disposable::dispose);
    }

    private <T> AtomicReference<ListEvent<T>>
    captureEvent(Observable<ListEvent<T>> eventSource) {
        AtomicReference<ListEvent<T>> eventAtomicReference = new AtomicReference<>();
        register(eventSource
                .doOnNext(eventAtomicReference::set)
                .subscribe()
        );
        return eventAtomicReference;
    }

    @Test
    void testAdd() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.additions());
        list.add("Catelyn");
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Catelyn"));
        assertThat(evt.position(), is(8));
        assertThat(evt.affected(), is(1));
        assertThat(evt.eventType(), is(ADDITION));
    }

    @Test
    void testAddAll() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.additions());
        list.addAll(asList("Jaime", "Tirion", "Myrcella"));
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Jaime", "Tirion", "Myrcella"));
        assertThat(evt.affected(), is(3));
        assertThat(evt.source(),
                contains("Sansa", "Robb", "Arya", "Jon", "Rickon", "Bran", "Ned", "Jaime", "Tirion", "Myrcella"));

        assertThat(evt.eventType(), is(ADDITION));
    }

    @Test
    void testAddAllWithIndex() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.additions());
        list.addAll(4, asList("Jaime", "Tirion", "Myrcella"));
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Jaime", "Tirion", "Myrcella"));
        assertThat(evt.affected(), is(3));
        assertThat(evt.position(), is(4));
        assertThat(evt.source(),
                contains("Sansa", "Robb", "Arya", "Jon", "Jaime", "Tirion", "Myrcella", "Rickon", "Bran", "Ned"));

        assertThat(evt.eventType(), is(ADDITION));
    }

    @Test
    void testRemove() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.deletions());
        // Say goodbye to Ned ;)
        list.remove("Ned");
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Ned"));
        assertThat(evt.affected(), is(1));
        assertThat(evt.position(), is(6));

        assertThat(evt.eventType(), is(DELETION));
    }

    @Test
    void testRemoveAll() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.deletions());
        // Say goodbye to mens x(
        list.removeAll(asList("Robb", "Jon", "Rickon", "Bran", "Ned"));
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Robb", "Jon", "Rickon", "Bran", "Ned"));
        assertThat(evt.affected(), is(5));
        assertThat(evt.position(), is(-1));
        assertThat(evt.source(), contains("Sansa", "Arya"));

        assertThat(evt.eventType(), is(DELETION));
    }

    @Test
    void testRetainAll() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.deletions());
        // Girls rules !
        list.retainAll(asList("Sansa", "Arya"));
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Robb", "Jon", "Rickon", "Bran", "Ned"));
        assertThat(evt.affected(), is(5));
        assertThat(evt.position(), is(-1));
        assertThat(evt.source(), contains("Sansa", "Arya"));

        assertThat(evt.eventType(), is(DELETION));
    }

    @Test
    void testClear() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.deletions());
        // Valar morghulis !
        list.clear();
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Sansa", "Robb", "Arya", "Jon", "Rickon", "Bran", "Ned"));
        assertThat(evt.affected(), is(7));
        assertThat(evt.position(), is(-1));
        assertThat(evt.source(), empty());

        assertThat(evt.eventType(), is(DELETION));
    }

    // Delegate method of List
    @Test
    void testGet() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        assertThat(list.get(1), is("Robb"));
        assertThat(list.get(3), is("Jon"));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(9));
    }

    @Test
    void testSet() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.modifications());

        list.set(6, "Catelyn");
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Catelyn"));
        assertThat(evt.affected(), is(1));
        assertThat(evt.position(), is(6));

        assertThat(evt.source(),
                contains("Sansa", "Robb", "Arya", "Jon", "Rickon", "Bran", "Catelyn"));

        assertThat(evt.eventType(), is(MODIFICATION));
    }

    @Test
    void testAddWithIndex() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.additions());
        list.add(5, "Catelyn");
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Catelyn"));
        assertThat(evt.position(), is(5));
        assertThat(evt.affected(), is(1));

        assertThat(evt.source(),
                contains("Sansa", "Robb", "Arya", "Jon", "Rickon", "Catelyn", "Bran", "Ned"));

        assertThat(evt.eventType(), is(ADDITION));
    }

    @Test
    void testRemoveWithIndex() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        AtomicReference<ListEvent<String>> ref = captureEvent(list.deletions());

        list.remove(1); // Bye Robb
        final ListEvent<String> evt = ref.get();

        assertThat(evt.items(), contains("Robb"));
        assertThat(evt.position(), is(1));
        assertThat(evt.affected(), is(1));

        assertThat(evt.source(),
                contains("Sansa", "Arya", "Jon", "Rickon", "Bran", "Ned"));

        assertThat(evt.eventType(), is(DELETION));
    }

    // Delegate test
    @Test
    void testIndexOf() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        assertThat(list.indexOf("Sansa"), is(0));
        assertThat(list.indexOf("Ned"), is(6));
        assertThat(list.indexOf("Arya"), is(2));
        assertThat(list.indexOf("Theon"), is(-1));
    }

    @Test
    void subList() {
        // Sansa, Robb, Arya, Jon, Rickon, Bran, Ned
        // -> Arya, Jon
        List<String> subList = list.subList(2, 4);

        assertThat(subList, instanceOf(ReactiveList.class));
        if (!(subList instanceof ReactiveList))
            fail("Test cannot continue because subList is not instance of ReactiveList");

        ReactiveList<String> rxSubList = (ReactiveList<String>) subList;

        assertThat(rxSubList.size(), is(2));
        assertThat(rxSubList, contains("Arya", "Jon"));
    }
}
