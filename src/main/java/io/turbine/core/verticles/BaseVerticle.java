package io.turbine.core.verticles;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

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
     * A chain of completable for initializing the verticles inheritance hierarchy
     */
    private List<Completable> initChain;

    /**
     * Initialize the Verticle, called by Vert.x
     * @param vertx  the deploying Vert.x instance
     * @param context  the context of the verticle
     */
    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        reader = new Reader(config());
        initChain = new LinkedList<>();
        initialize();
        processInitializationChain();
    }

    private void processInitializationChain() throws InitializationException {
        initChain.stream()
                .reduce(Completable::andThen)
                .orElseThrow(() -> new InitializationException(
                        new IllegalStateException("Could not aggregate verticle hierarchy initialization completables"),
                        this
                ))
                .subscribe(() -> logger.info("Verticle {} is READY !", getClass().getSimpleName()));
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
    protected final void doOnInitialize(Completable completable) {
        requireNonNull(completable, "completable");
        initChain.add(completable);
    }

    protected void initialize() {}

    public Flowable<Throwable> serverErrors() {
        return serverErrors.toFlowable(BackpressureStrategy.DROP);
    }
}
