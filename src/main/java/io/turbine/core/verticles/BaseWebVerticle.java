package io.turbine.core.verticles;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.turbine.core.errors.exceptions.http.HttpException;
import io.turbine.core.errors.exceptions.http.ServerErrorException;
import io.turbine.core.json.JsonUtils;
import io.turbine.core.model.web.handlers.ResponseAdapter;
import io.turbine.core.model.web.handlers.ResponsePrinter;
import io.turbine.core.model.web.handlers.RxRequestHandler;
import io.turbine.core.model.web.router.ReactiveRouter;
import io.turbine.core.model.web.router.Response;
import io.turbine.core.verticles.behaviors.WebVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

import static io.reactivex.Single.just;


/**
 * TODO Docstrings to rewrite *
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
        register(serverErrors().subscribe(this::handleServerErrors));
    }

    private void handleServerErrors(Throwable t) {
        if (t instanceof ServerErrorException) {
            String uuid = ((ServerErrorException) t).getUuid();
            logger.error("A server error exception has occurred : " + uuid, t.getCause());
        }
    }

    protected Consumer<? super HttpServer> logServerStart() {
        return server -> logger.info("Server started listening at port " + server.actualPort());
    }

    /**************************
     *  Response methods
     */

    protected Consumer<RoutingContext>
    jsonResponse(RxRequestHandler<Object> requestHandler)
    {
        return response(
                ResponseAdapter.jsonAdapter(),
                JsonUtils::printJson,
                requestHandler);
    }



    protected <Rp> Consumer<RoutingContext>
    response(RxRequestHandler<Object> requestHandler) {
        return response(
                ResponseAdapter.plainTextAdapter(),
                Object::toString,
                requestHandler);
    }

    private <Rp> Consumer<RoutingContext>
    response(ResponseAdapter adapter,
             ResponsePrinter<Rp> printer,
             RxRequestHandler<Rp> rxRequestHandler) {
        return rc -> {
            Single<Response<Rp>> response = rxRequestHandler.apply(rc);

            adapter.accept(rc.response());
            response.subscribe(rp ->
                    writeResponse(rc, printer.apply(rp.body()), rp.statusCode()));
        };
    }

    private void writeResponse(RoutingContext rc, String body, int statusCode) {
        rc  .response()
            .setStatusCode(statusCode)
            .end(body);
    }

    /*
     * Exception Handlers
     */
    protected Single<Response<Object>> handleException(Throwable t)  {
        try {
            try {
                throw t;
            } catch (HttpException httpEx) {
                throw httpEx;
            } catch (Throwable internalEx) {
                throw new ServerErrorException(internalEx);
            }
        } catch (HttpException httpEx) {
            if (httpEx instanceof ServerErrorException)
                serverErrors.onNext(httpEx);

            return just(
                    new Response<>(httpEx, httpEx.statusCode()));
        }
    }
}
