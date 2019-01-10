package io.turbine.core.verticles.behaviors;

import io.turbine.core.web.router.ReactiveRouter;
import io.vertx.reactivex.ext.web.handler.CorsHandler;

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

    /**
     * Defines a CORS handler for the Web Verticle.
     * @return An instance of CorsHandler
     */
    CorsHandler corsHandler();
}
