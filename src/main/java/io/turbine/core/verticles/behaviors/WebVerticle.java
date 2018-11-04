package io.turbine.core.verticles.behaviors;

import io.turbine.core.web.router.ReactiveRouter;

/**
 * Defines the behavioral specification of a Web verticle, which means a verticle
 * that provide Web reactive serving capabilities.
 * A web verticle must provide a ReactiveRouter instance.
 *
 * @see ReactiveRouter
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface WebVerticle extends HttpVerticle {
    /**
     * Gives an instance of a reactive router.
     * @return A ReactiveRouter instance
     */
    ReactiveRouter router();
}
