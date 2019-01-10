package io.turbine.core.verticles;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.turbine.core.errors.exceptions.http.HttpException;
import io.turbine.core.errors.exceptions.http.ServerErrorException;
import io.turbine.core.json.JsonFormat;
import io.turbine.core.verticles.behaviors.WebVerticle;
import io.turbine.core.web.handlers.RequestHandler;
import io.turbine.core.web.handlers.ResponseAdapter;
import io.turbine.core.web.handlers.ResponsePrinter;
import io.turbine.core.web.handlers.ResponseTypeEnum;
import io.turbine.core.web.mapping.RequestHandling;
import io.turbine.core.web.mapping.RequestHandlingMapper;
import io.turbine.core.web.mapping.ResponseType;
import io.turbine.core.web.router.ReactiveRouter;
import io.turbine.core.web.router.Response;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CorsHandler;

import java.lang.reflect.Method;
import java.util.Map;

import static io.reactivex.Single.just;
import static io.turbine.core.utils.Utils.orElseGet;
import static io.turbine.core.web.router.Response.ok;


/**
 * TODO Docstrings to rewrite *
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 * @see BaseHttpVerticle
 * @see ReactiveRouter
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
     *
     * @param vertx   the deploying Vert.x instance
     * @param context the context of the verticle
     */
    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        if (router == null) {
            router = new ReactiveRouter(Router.router(this.vertx));
            router.route().handler(BodyHandler.create());
            if (corsHandler() != null) {
                router.route().handler(corsHandler());
            }
        }
        applyRequestMappings();
    }

    @Override
    public CorsHandler corsHandler() {
        return null;
    }

    private void applyRequestMappings() {
        Map<RequestHandling, Method> mappings =
                RequestHandlingMapper.findMappings(this, true);

        mappings.forEach((mapping, method) -> {
            Flowable<RoutingContext> flowable = router()
                .route(mapping.method(), mapping.path())
                .toFlowable(mapping.strategy());

            @SuppressWarnings("unchecked") final RequestHandler requestHandler = (rc) -> {
                try {
                    /* In case of raw response (not wrapped in a Response object),
                     * we create a 200 OK by default */
                    Single single = (Single) method.invoke(this, rc);
                    return single.map(value ->
                            (value instanceof Response) ? value : ok(value));
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException("Unable to call the action method " + method + ".", ex);
                }
            };

            ResponseType responseTypeAnno = method.getAnnotation(ResponseType.class);
            ResponseTypeEnum responseType =
                    orElseGet(responseTypeAnno, ResponseType::value, ResponseTypeEnum.JSON);

            register(
                    flowable.doOnNext( getSuitableResponseTypeHandler(responseType, requestHandler))
                            .subscribe());
            logger.info("Mapped route {} {} to method {}() (using {} strategy).",
                    mapping.method(), mapping.path(), method.getName(), mapping.strategy());
        });
        logger.info("Found {} request handling mapping(s) for this verticle", mappings.size());
    }

    private Consumer<RoutingContext>
    getSuitableResponseTypeHandler(ResponseTypeEnum responseType, RequestHandler requestHandler)
    {
        switch (responseType) {
            case XML: return xmlResponse(requestHandler);
            case TEXT: return textResponse(requestHandler);
            default:
                return jsonResponse(requestHandler);
        }
    }

    /**************************
     *  Response methods
     */

    protected Consumer<RoutingContext>
    jsonResponse(RequestHandler requestHandler) {
        return response(
                ResponseAdapter.jsonAdapter(),
                JsonFormat::printJson,
                requestHandler);
    }

    protected Consumer<RoutingContext>
    xmlResponse(RequestHandler requestHandler) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    protected Consumer<RoutingContext>
    textResponse(RequestHandler requestHandler) {
        return response(
                ResponseAdapter.plainTextAdapter(),
                Object::toString,
                requestHandler);
    }

    // TODO create response method overloads with exception handler
    private Consumer<RoutingContext>
    response(ResponseAdapter adapter,
             ResponsePrinter printer,
             RequestHandler requestHandler) {
        return rc -> {
            Single<Response> response = requestHandler.apply(rc);

            adapter.accept(rc.response());
            register(response.subscribe(rp ->
                    writeResponse(rc, printer.apply(rp.body()), rp.statusCode())
            ));
        };
    }

    private void writeResponse(RoutingContext rc, String body, int statusCode) {
        rc.response()
                .setStatusCode(statusCode)
                .end(body);
    }

    /*
     * Exception Handlers
     */
    // TODO Generalize exception handling to object instead of only provinding response
    // TODO Automatically call onErrorResumeNext() on Single returned by request mapping action method
    protected Single<Response> defaultExceptionHandler(Throwable t) {
        try {
            try {
                throw t;
            } catch (HttpException httpEx) {
                throw httpEx;
            } catch (Throwable internalEx) {
                throw new ServerErrorException(internalEx);
            }
        } catch (HttpException httpEx) {
            if (httpEx instanceof ServerErrorException) {
                String uuid = ((ServerErrorException) httpEx).getUuid();
                logger.error("A server error exception has occurred : " + uuid, t.getCause());
            }

            return just(
                    new Response(httpEx, httpEx.statusCode()));
        }
    }
}
