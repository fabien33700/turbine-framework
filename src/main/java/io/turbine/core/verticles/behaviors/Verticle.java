package io.turbine.core.verticles.behaviors;

import io.reactivex.Completable;
import io.turbine.core.utils.Composite;

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
}
