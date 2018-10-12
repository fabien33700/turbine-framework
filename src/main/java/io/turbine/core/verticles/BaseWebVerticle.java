package io.turbine.core.verticles;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.turbine.core.model.web.router.ReactiveRouter;
import io.turbine.core.verticles.behaviors.WebVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

/**
 * Defines a base implementation for all Web verticles in the application.
 * Web verticles are HTTP verticles that provide also routing capabilities.
 *
 * This base manages the instanciation of a reactive router, and supplies convenients
 * methods that returns functions to use with the Reactive Router and the RX operators.
 *
 * Different overloads of response() method allows to define how to handle client's requests and
 * how to respond to them. The operation follows these steps :
 *
 * #1. Case of a request for which body is deserialized to a domain model class
 * | RoutingContext --> [ModelReader] --> Instance of model M --> [ModelRequestHandler]
 *      --> response value R            \
 *      =======================          --> Response<R> --> [ResponsePrinter<R>] --> String
 *      --> error HttpErrorException    /
 *
 * #2. Case of a request for which body is directly passed to the request handler
 * | RoutingContext --> [RequestHandler]
 *      --> response value R            \
 *      =======================          --> Response<R> --> [ResponsePrinter<R>] --> String
 *      --> error HttpErrorException    /
 *
 * - RoutingContext is the representation of the received client request
 * - ModelReader has to convert the request body to a model domain instance M
 * - (Model)RequestHandler has to give a response R or throw an Exception from M / RoutingContext
 * - Response value or caught Exception are wrapped in a Response instance
 *  (Exception will be converted to an HttpErrorException by an ExceptionHandler
 * - ResponsePrinter has to convert R response value / HttpErrorException to String.
 *
 * By concatenating all these operations, we obtain functions that take a RoutingContext and give
 * a response as string.
 *
 *
 * @see BaseHttpVerticle
 * @see ReactiveRouter
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public abstract class BaseWebVerticle extends BaseHttpVerticle implements WebVerticle {

    /**
     * The router instance
     */
    private ReactiveRouter router;

    /**
     * {@inheritDoc}
     */
    @Override
    public final ReactiveRouter router() {
        return router;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Single<HttpServer> listen(int port) {
        return httpServer()
            .requestHandler(router::accept)
            .rxListen(port);
    }

    /**
     * {@inheritDoc}
     * Initialize also the reactive router instance.
     * @param vertx  the deploying Vert.x instance
     * @param context  the context of the verticle
     */
    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        if (router == null) {
            router = new ReactiveRouter(Router.router(this.vertx));
            router.route().handler(BodyHandler.create());
        }
    }

    protected Consumer<? super HttpServer> logServerStarted() {
        return server -> logger.info("Server started listening at port " + server.actualPort());
    }

}
