package io.turbine.core.errors.exceptions.request;

import io.vertx.reactivex.ext.web.RoutingContext;

public class RequestHandlingException extends RequestException {
    private static final String DEFAULT_MESSAGE = "An exception has occurred during handling of data extracted from request.";

    public RequestHandlingException(Throwable cause, RoutingContext routingContext) {
        super(DEFAULT_MESSAGE, cause, routingContext);
    }
}
