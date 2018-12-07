package io.turbine.core.web.router;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.web.Route;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.List;


public class ReactiveRouter {

    private final Router delegate;

    public ReactiveRouter(Router delegate) {
        this.delegate = delegate;
    }

    /** Delegate routes methods */
    public ReactiveRoute route() {
        return new ReactiveRoute(delegate.route());
    }

    public ReactiveRoute route(HttpMethod method, String path) {
        return new ReactiveRoute(delegate.route(method, path));
    }

    public ReactiveRoute route(String path) {
        return new ReactiveRoute(delegate.route(path));
    }

    public ReactiveRoute routeWithRegex(HttpMethod method, String regex) {
        return new ReactiveRoute(delegate.routeWithRegex(method, regex));
    }


    public ReactiveRoute routeWithRegex(String regex) {
        return new ReactiveRoute(delegate.routeWithRegex(regex));
    }


    public ReactiveRoute get() {
        return new ReactiveRoute(delegate.get());
    }


    public ReactiveRoute get(String path) {
        return new ReactiveRoute(delegate.get(path));
    }


    public ReactiveRoute getWithRegex(String regex) {
        return new ReactiveRoute(delegate.getWithRegex(regex));
    }


    public ReactiveRoute head() {
        return new ReactiveRoute(delegate.head());
    }


    public ReactiveRoute head(String path) {
        return new ReactiveRoute(delegate.head(path));
    }


    public ReactiveRoute headWithRegex(String regex) {
        return new ReactiveRoute(delegate.headWithRegex(regex));
    }


    public ReactiveRoute options() {
        return new ReactiveRoute(delegate.options());
    }


    public ReactiveRoute options(String path) {
        return new ReactiveRoute(delegate.options(path));
    }


    public ReactiveRoute optionsWithRegex(String regex) {
        return new ReactiveRoute(delegate.optionsWithRegex(regex));
    }


    public ReactiveRoute put() {
        return new ReactiveRoute(delegate.put());
    }


    public ReactiveRoute put(String path) {
        return new ReactiveRoute(delegate.put(path));
    }


    public ReactiveRoute putWithRegex(String regex) {
        return new ReactiveRoute(delegate.putWithRegex(regex));
    }


    public ReactiveRoute post() {
        return new ReactiveRoute(delegate.post());
    }


    public ReactiveRoute post(String path) {
        return new ReactiveRoute(delegate.post(path));
    }


    public ReactiveRoute postWithRegex(String regex) {
        return new ReactiveRoute(delegate.postWithRegex(regex));
    }


    public ReactiveRoute delete() {
        return new ReactiveRoute(delegate.delete());
    }


    public ReactiveRoute delete(String path) {
        return new ReactiveRoute(delegate.delete(path));
    }


    public ReactiveRoute deleteWithRegex(String regex) {
        return new ReactiveRoute(delegate.deleteWithRegex(regex));
    }


    public ReactiveRoute trace() {
        return new ReactiveRoute(delegate.trace());
    }


    public ReactiveRoute trace(String path) {
        return new ReactiveRoute(delegate.trace(path));
    }


    public ReactiveRoute traceWithRegex(String regex) {
        return new ReactiveRoute(delegate.traceWithRegex(regex));
    }


    public ReactiveRoute connect() {
        return new ReactiveRoute(delegate.connect());
    }


    public ReactiveRoute connect(String path) {
        return new ReactiveRoute(delegate.connect(path));
    }


    public ReactiveRoute connectWithRegex(String regex) {
        return new ReactiveRoute(delegate.connectWithRegex(regex));
    }


    public ReactiveRoute patch() {
        return new ReactiveRoute(delegate.patch());
    }


    public ReactiveRoute patch(String path) {
        return new ReactiveRoute(delegate.patch(path));
    }


    public ReactiveRoute patchWithRegex(String regex) {
        return new ReactiveRoute(delegate.patchWithRegex(regex));
    }

    /** Unchanged delegate methods **/
    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Router && delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    public io.vertx.ext.web.Router getDelegate() {
        return delegate.getDelegate();
    }

    public static Router router(Vertx vertx) {
        return Router.router(vertx);
    }

    public void accept(HttpServerRequest request) {
        delegate.accept(request);
    }

    public List<Route> getRoutes() {
        return delegate.getRoutes();
    }

    public Router clear() {
        return delegate.clear();
    }

    public Router mountSubRouter(String mountPoint, Router subRouter) {
        return delegate.mountSubRouter(mountPoint, subRouter);
    }

    public Router exceptionHandler(Handler<Throwable> exceptionHandler) {
        return delegate.exceptionHandler(exceptionHandler);
    }

    public void handleContext(RoutingContext context) {
        delegate.handleContext(context);
    }

    public void handleFailure(RoutingContext context) {
        delegate.handleFailure(context);
    }

    public static Router newInstance(io.vertx.ext.web.Router arg) {
        return Router.newInstance(arg);
    }
}
