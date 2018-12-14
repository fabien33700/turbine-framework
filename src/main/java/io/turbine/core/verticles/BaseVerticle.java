package io.turbine.core.verticles;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.turbine.core.configuration.Dispatcher;
import io.turbine.core.configuration.Reader;
import io.turbine.core.deployment.VerticleDeployer;
import io.turbine.core.deployment.VerticleFactory;
import io.turbine.core.utils.Composite;
import io.turbine.core.verticles.behaviors.Verticle;
import io.turbine.core.verticles.support.Parameter;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

import static io.reactivex.Completable.complete;
import static io.reactivex.Completable.fromAction;
import static io.turbine.core.deployment.VerticleFactory.fromClass;
import static io.turbine.core.deployment.VerticleFactory.fromSupplier;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.ofNullable;

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
public abstract class BaseVerticle extends AbstractVerticle implements Verticle, Composite<BaseVerticle> {

    /**
     * The verticle logger instance
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

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
    private final List<BaseVerticle> children = new ArrayList<>();

    /**
     * A reference to the parent verticle
     */
    private BaseVerticle parent;

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

    @Override
    public final JsonObject config() {
        return Dispatcher.getInstance().dispatch(super.config(), getClass());
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

    private Completable getVerticlesChildrenCompletables() {
        return children.stream()
                .map(BaseVerticle::rxStop)
                .reduce(Completable::concatWith)
                .orElse(Completable.complete());
    }

    protected final VerticleDeployer deployer() {
        return VerticleDeployer.getInstance();
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
        return complete();
    }

    @Override
    public Completable rxStop() {
        return getVerticlesChildrenCompletables().concatWith(
                fromAction(() -> subscriptions.forEach(Disposable::dispose)) );
    }

    @Override
    public final void inject(Map<String, Object> parameters) {
        for (Field field : getClass().getDeclaredFields()) {
            if (parameters.containsKey(field.getName()) &&
                    field.isAnnotationPresent(Parameter.class)) {
                final String name = field.getName();
                try {
                    field.setAccessible(true);
                    if (field.getType().equals(Integer.class)) {
                        field.setInt(this, (int) parameters.get(name));
                    } else if (field.getType().equals(Boolean.class)) {
                        field.setBoolean(this, (boolean) parameters.get(name));
                    } else if (field.getType().equals(Long.class)) {
                        field.setLong(this, (long) parameters.get(name));
                    } else if (field.getType().equals(Float.class)) {
                        field.setFloat(this, (float) parameters.get(name));
                    } else if (field.getType().equals(Double.class)) {
                        field.setDouble(this, (double) parameters.get(name));
                    } else if (field.getType().equals(String.class)) {
                        field.set(this, parameters.get(name));
                    }
                } catch (ClassCastException | ReflectiveOperationException ex) {
                    throw new RuntimeException("Cannot set parameter " + name, ex);
                } finally {
                    field.setAccessible(false);
                }
            }
        }
    }

    public final <V extends BaseVerticle>
    V deployVerticle(Class<V> verticleClass, Map<String, Object> parameters) {
        return deployVerticle(fromClass(verticleClass), parameters);
    }

    public final <V extends BaseVerticle>
    V deployVerticle(Supplier<V> verticleSupplier, Map<String, Object> parameters) {
        return deployVerticle(fromSupplier(verticleSupplier), parameters);
    }

    private <V extends BaseVerticle>
    V deployVerticle(VerticleFactory<V> verticleFactory, Map<String, Object> parameters) {
        V verticle = deployer().deployVerticle(verticleFactory, parameters);

        if (verticle == null) {
            throw new RuntimeException("Deployment of " + verticleFactory.verticleName() + " had failed.");
        }

        verticle.setParent(this);
        children.add(verticle);

        return verticle;
    }

    public void setParent(BaseVerticle verticle) {
        this.parent = verticle;
    }

    @Override
    public List<BaseVerticle> getChildren() {
        return unmodifiableList(children);
    }

    @Override
    public Optional<BaseVerticle> getParent() {
        return ofNullable(parent);
    }
}
