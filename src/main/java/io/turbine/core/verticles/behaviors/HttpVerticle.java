package io.turbine.core.verticles.behaviors;

import io.reactivex.Single;
import io.turbine.core.verticles.BaseHttpVerticle;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.reactivex.core.http.HttpServer;

/**
 * Defines the behavioral specification of a verticle which provides
 * HTTP serving capabilities.
 * A HTTPVerticle must manage a server instance, server options,
 * the use or not of SSL, and the port.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface HttpVerticle {
    /**
     * Returns an instance of a HTTP server.
     * @return a Vert.x RX HttpServer instance
     */
    HttpServer httpServer();

    /**
     * Build a HttpServerOptions instance according to the verticle
     * configuration.
     * @return A Vert.x RX HttpServerOptions instance
     */
    HttpServerOptions httpServerOptions();

    /**
     * Build a JksOptions instance according to the verticle
     * configuration.
     * Used by SSL secured Http Verticle.
     * @return A Vert.x RX JksOptions instance
     */
    JksOptions jksOptions();

    /**
     * Returns a reactive single value response of the HTTP server
     * after it has begun listening the port specified by port() method.
     * @see Single
     * @see BaseHttpVerticle#port()
     * @return A Single of Vert.x RX HttpServer instance
     */
    Single<HttpServer> listen();

    /**
     * Returns a reactive single value response of the HTTP server
     * after it has begun listening the given port.
     * @param port The port to listen to
     * @see Single
     * @return A Single of Vert.x RX HttpServer instance
     */
    Single<HttpServer> listen(int port);

    /**
     * Indicates whether the server will use SSL secured connection,
     * according to the configuration.
     * @return true if it uses SSL, false if it doesn't.
     */
    boolean useSsl();

    /**
     * Gives the default port which the verticle will listen to.
     * @return The default port to listen
     */
    int port();
}
