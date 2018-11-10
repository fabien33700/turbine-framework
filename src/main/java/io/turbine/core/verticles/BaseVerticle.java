package io.turbine.core.verticles;

import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import io.turbine.core.configuration.Dispatcher;
import io.turbine.core.configuration.Reader;
import io.turbine.core.errors.exceptions.verticles.InitializationException;
import io.turbine.core.logging.Logger;
import io.turbine.core.logging.LoggerFactory;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import static io.reactivex.Completable.*;
import static java.util.Objects.requireNonNull;

/**
 * Defines a base implementation for all the verticles in the application.
 *
 * This base provides convenient base features: logging, configuration
 * reading and reactive subscriptions self-disposing.
 *
 * It is built upon the Vert.x AbstractVerticle specification.
 *
 * @see AbstractVerticle
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public abstract class BaseVerticle extends AbstractVerticle {

    /**
     * A chain of completable for initializing the verticles inheritance hierarchy
     */
    public class CompletableChain {
        private List<Completable> chain = new LinkedList<>();

        public CompletableChain append(Completable completable) {
            requireNonNull(completable, "completable");
            chain.add(completable);

            return this;
        }

        public CompletableChain append(Action action) {
            requireNonNull(action, "action");
            return append(fromAction(action));
        }

        public CompletableChain append(SingleSource<?> single) {
            requireNonNull(single, "single");
            return append(fromSingle(single));
        }

        public CompletableChain append(Callable<?> callable) {
            requireNonNull(callable, "callable");
            return append(fromCallable(callable));
        }

        public CompletableChain append(java.util.concurrent.Future<?> future) {
            requireNonNull(future, "future");
            return append(fromFuture(future));
        }

        public CompletableChain append(Observable<?> single) {
            requireNonNull(single, "single");
            return append(fromObservable(single));
        }

        public CompletableChain append(Publisher<?> publisher) {
            requireNonNull(publisher, "publisher");
            return append(fromPublisher(publisher));
        }

        public CompletableChain append(Runnable runnable) {
            requireNonNull(runnable, "runnable");
            return append(fromRunnable(runnable));
        }

        public Completable reduce() {
            return chain.stream()
                .reduce(Completable::andThen)
                .orElseGet(Completable::complete);
        }
    }

    protected Subject<Throwable> serverErrors = BehaviorSubject.create();

    /**
     * The verticle logger instance
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * A list for storing all the verticles subscriptions to observables
     */
    private List<Disposable> subscriptions = new ArrayList<>();

    /**
     * The configuration reader
     */
    private Reader reader;

    /**
     * Initialize the Verticle, called by Vert.x
     * @param vertx  the deploying Vert.x instance
     * @param context  the context of the verticle
     */
    @Override
    public void init(Vertx vertx, Context context) throws InitializationException {
        super.init(vertx, context);
        reader = new Reader(config());
    }

    @Override
    public final JsonObject config() {
        return Dispatcher.getInstance().dispatch(super.config(), getClass());
    }

    /**
     * A method to register a Disposable into verticle subscriptions.
     * @param disposable The disposable to register
     * @param <D> The type of Disposable
     * @return The registered Disposable instance
     */
    protected <D extends Disposable> D register(D disposable) {
        subscriptions.add(disposable);
        return disposable;
    }

    /**
     * A delegate method to the configuration reader read method with default value
     * @param path A dot-separated access path to the property
     * @param defaultValue The default value if no property was found for the given key
     * @param <T> The type of the value to read (inferred with the given default value)
     * @return The property value
     */
    protected final <T> T readConfig(String path, T defaultValue) {
        return reader.read(path, defaultValue);
    }

    /**
     * A delegate method to the configuration reader read method
     * @param path A dot-separated access path to the property
     * @param <T> The type of the value to read (inferred with the given default value)
     * @throws NoSuchElementException Thrown if no property was found for the given path
     * @return The property value
     */
    protected final <T> T readConfig(String path) throws NoSuchElementException {
        return reader.read(path);
    }

    @Override
    public void start(Future<Void> startFuture) {
        initialize().reduce()
            .doOnComplete(() -> logger.info("Verticle {} is READY !", getClass().getSimpleName()))
            .doOnError(Throwable::printStackTrace)
            .subscribe();
    }

    /**
     * Stop the verticle.
     * @param stopFuture The future for stop operation
     */
    @Override
    public void stop(Future<Void> stopFuture) {
        // This ensure verticle will dispose all its subscriptions before stopping
        subscriptions.forEach(Disposable::dispose);
        stopFuture.complete();
    }

    /**
     * Allows to define a Completable task that will be execute for Verticle initialization.
     */
    protected CompletableChain initialize() {
        return new CompletableChain();
    }

    public Flowable<Throwable> serverErrors() {
        return serverErrors.toFlowable(BackpressureStrategy.DROP);
    }
}
