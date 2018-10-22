package io.turbine.core.errors.exceptions.request;

import io.vertx.reactivex.ext.web.RoutingContext;

public abstract class RequestException extends RuntimeException {

    private final RoutingContext routingContext;

    protected RequestException(String message, Throwable cause, RoutingContext routingContext) {
        super(message, cause);
        this.routingContext = routingContext;
    }

    public RoutingContext getRoutingContext() {
        return routingContext;
    }
}
