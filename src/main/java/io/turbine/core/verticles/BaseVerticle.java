package io.turbine.core.verticles;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.turbine.core.configuration.Reader;
import io.turbine.core.deployment.Turbine;
import io.turbine.core.deployment.VerticleDeployer;
import io.turbine.core.deployment.annotations.Shared;
import io.turbine.core.verticles.behaviors.Verticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.shareddata.AsyncMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static io.reactivex.Completable.*;
import static io.turbine.core.utils.Utils.Reflection.setFieldValue;
import static io.turbine.core.utils.Utils.Strings.isEmpty;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;

/**
 * Defines a base implementation for all the verticles in the application.
 * <p>
 * This base provides convenient base features: logging, configuration
 * reading and reactive subscriptions self-disposing.
 * <p>
 * It is built upon the Vert.x AbstractVerticle specification.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 * @see AbstractVerticle
 */
public abstract class BaseVerticle extends AbstractVerticle implements Verticle {

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
     * A list of children verticles deployed by the current verticle
     */
    /* FIXME, Replace List by a Map with deploymentId as key */
    private final List<Verticle> children = new ArrayList<>();

    /**
     * A reference to the parent verticle
     */
    private Verticle parent;

    /**
     * The verticle configuration
     */
    private JsonObject config;

    /**
     * Initialize the Verticle, called by Vert.x
     *
     * @param vertx   the deploying Vert.x instance
     * @param context the context of the verticle
     */
    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        reader = new Reader(config());
    }

    /**
     * Change the logger's name.
     *
     * @param name The new logger name
     */
    protected final void setLoggerName(String name) {
        logger = LoggerFactory.getLogger(name);
    }

    /**
     * A method to register a Disposable into verticle subscriptions.
     *
     * @param <D>        The type of Disposable
     * @param disposable The disposable to register
     */
    protected <D extends Disposable> void register(D disposable) {
        subscriptions.add(disposable);
    }

    @SafeVarargs
    protected final <D extends Disposable> void register(D... disposables) {
        subscriptions.addAll(asList(disposables));
    }

    /**
     * A delegate method to the configuration reader read method with default value
     *
     * @param path         A dot-separated access path to the property
     * @param defaultValue The default value if no property was found for the given key
     * @param <T>          The type of the value to read (inferred with the given default value)
     * @return The property value
     */
    protected final <T> T readConfig(String path, T defaultValue) {
        return reader.read(path, defaultValue);
    }

    /**
     * A delegate method to the configuration reader read method
     *
     * @param path A dot-separated access path to the property
     * @param <T>  The type of the value to read (inferred with the given default value)
     * @return The property value
     * @throws NoSuchElementException Thrown if no property was found for the given path
     */
    protected final <T> T readConfig(String path) throws NoSuchElementException {
        return reader.read(path);
    }

    private Completable allSubVerticlesStop() {
        return children.stream()
                .map(Verticle::rxStop)
                .reduce(Completable::concatWith)
                .orElse(complete());
    }

    protected final VerticleDeployer deployer() {
        return Turbine.getDeployer();
    }

    @Override
    public final void start(Future<Void> startFuture) {
        rxStart().subscribe(startFuture::complete, startFuture::fail);
    }

    @Override
    public final void stop(Future<Void> stopFuture) {
        rxStop().subscribe(stopFuture::complete, stopFuture::fail);
    }


    @Override
    public Completable rxStart() {
        return getSharedData();
    }

    private Completable getSharedData() {
        Completable loaded = complete();
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Shared.class)
                    && field.getType().isAssignableFrom(AsyncMap.class)) {
                final String name;
                if (isEmpty(field.getAnnotation(Shared.class).name()))
                    name = field.getName();
                else
                    name = field.getAnnotation(Shared.class).name();

                loaded = loaded.concatWith(vertx.sharedData().rxGetAsyncMap(name)
                        .flatMapCompletable(asyncmap -> {
                            try {
                                setFieldValue(this, field, asyncmap);
                                logger.debug("'{}' shared async map loaded", name);
                                return complete();
                            } catch (ReflectiveOperationException ex) {
                                logger.warn("Could not inject the shared async map {} into the verticle {} field",
                                        name, getClass().getSimpleName());
                                return error(ex);
                            }
                        }));
            }
        }
        return loaded;
    }

    @Override
    public Completable rxStop() {
        return allSubVerticlesStop().concatWith(
            fromAction(() -> subscriptions.forEach(Disposable::dispose)) );
    }

    @Override
    public void setParent(Verticle verticle) {
        this.parent = verticle;
    }

    @Override
    public Iterable<Verticle> getChildren() {
        return unmodifiableList(children);
    }

    @Override
    public Optional<Verticle> getParent() {
        return ofNullable(parent);
    }

    @Override
    public boolean hasParent() {
        return getParent().isPresent();
    }
}
