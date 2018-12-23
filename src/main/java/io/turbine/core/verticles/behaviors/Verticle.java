package io.turbine.core.verticles.behaviors;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.turbine.core.deployment.VerticleFactory;
import io.turbine.core.utils.Composite;
import io.vertx.core.json.JsonObject;

/**
 * Defines the root base of a Verticle behavior.
 * Note that this Verticle is distinct but also inherited from Vert.x one.
 * Verticle is a composite object, so it can contains other verticles and
 * be the child of another.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface Verticle extends io.vertx.core.Verticle, Composite<Verticle> {

    /**
     * A completable that represents the verticle starting computation chain.
     * @return A completable completed when the verticle has succeeded its starting process
     */
    Completable rxStart();

    /**
     * A completable that represents the verticle stopping computation chain.
     * @return A completable completed when the verticle has succeeded its stopping process
     */
    Completable rxStop();

    /**
     * Deploy a child verticle from the current verticle.
     * @param verticleClass The class of the verticle to deploy
     * @param config An additional configuration to specify
     * @param <V> The type of the verticle to deploy
     * @return A Single of the freshly-deployed Verticle instance
     */
    <V extends Verticle> Single<V>
    deployVerticle(Class<V> verticleClass, JsonObject config);

    /**
     * Deploy a child verticle from the current verticle.
     * @param factory A factory to instanciate the verticle
     * @param verticleClass The class of the verticle to deploy
     * @param config An additional configuration to specify
     * @param <V> The type of the verticle to deploy
     * @return A Single of the freshly-deployed Verticle instance
     */
    <V extends Verticle> Single<V>
    deployVerticle(VerticleFactory<V> factory, Class<V> verticleClass, JsonObject config);

}
