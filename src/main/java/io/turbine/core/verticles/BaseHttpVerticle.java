package io.turbine.core.verticles;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.turbine.core.configuration.Configuration;
import io.turbine.core.verticles.behaviors.HttpVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.reactivex.core.http.HttpServer;

import static io.reactivex.Completable.fromAction;
import static io.reactivex.Completable.fromSingle;


/**
* Defines a base implementation for all HTTP verticles in the application.
* HTTP verticles provide instances of Vert.x HTTP server.
*
* This base manages the instanciation of the http server, the setting up of
* http server options, such as port or Jks secured connection configuration
* according to the configuration.
*
* It is built upon the BaseVerticle specification, and so use scoped configuration
* provided by configuration classes Reader and Dispatcher.
* It will use Vert.x RX objects (io.vertx.reactivex.*)
*
* @see BaseVerticle
* @see io.turbine.core.configuration.Reader
* @see io.turbine.core.configuration.Dispatcher
* @see HttpServer
* @author Fabien <fabien DOT lehouedec AT gmail DOT com>
*/
@Configuration
public abstract class BaseHttpVerticle extends BaseVerticle implements HttpVerticle {

    /**
     * The HTTP server instance
     */
    private HttpServer httpServer;

    /**
     * {@inheritDoc}
     * Initialize also the HTTP server instance.
     * @param vertx  the deploying Vert.x instance
     * @param context  the context of the verticle
     */
    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        if (httpServer == null) {
            httpServer = this.vertx.createHttpServer(httpServerOptions());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpServer httpServer() {
        return httpServer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpServerOptions httpServerOptions() {
        final HttpServerOptions options = new HttpServerOptions();

        options.setSsl(useSsl());
        if (useSsl())
            options.setKeyStoreOptions(jksOptions());

        return options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Single<HttpServer> listen() {
        return listen(port());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Single<HttpServer> listen(int port) {
        return httpServer().rxListen(port);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JksOptions jksOptions() {
        final String keystore = readConfig("jks.keystore");
        final String passphrase = readConfig("jks.passphrase");

        return new JksOptions()
            .setPath(keystore)
            .setPassword(passphrase);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean useSsl() {
        return readConfig("use-ssl", false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int port() {
        /* Retrieving the port from the configuration.
           If there is no one, using 80 in HTTP and 443 in HTTPS. */
        int defaultPort = useSsl() ? 443 : 80;
        return readConfig("port", defaultPort);
    }

    @Override
    public Completable rxStart() {
        return super.rxStart()
                .concatWith(fromSingle(listen()))
                .concatWith(fromAction(() -> logger.info("Server listening at " + port()) ) );
    }
}
