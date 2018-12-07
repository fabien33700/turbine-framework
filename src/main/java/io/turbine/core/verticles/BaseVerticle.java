package io.turbine.core.verticles;

import io.reactivex.disposables.Disposable;
import io.turbine.core.configuration.Dispatcher;
import io.turbine.core.configuration.Reader;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
     * The verticle logger instance
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * A list for storing all the verticles subscriptions to observables
     */
    private final List<Disposable> subscriptions = new ArrayList<>();

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
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        reader = new Reader(config());
    }

    @Override
    public final JsonObject config() {
        return Dispatcher.getInstance().dispatch(super.config(), getClass());
    }

    /**
     * A method to register a Disposable into verticle subscriptions.
     * @param <D> The type of Disposable
     * @param disposable The disposable to register
     */
    protected <D extends Disposable> void register(D disposable) {
        subscriptions.add(disposable);
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
}
