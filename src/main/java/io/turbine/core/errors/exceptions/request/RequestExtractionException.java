package io.turbine.core.errors.exceptions.request;

import io.vertx.reactivex.ext.web.RoutingContext;

public class RequestExtractionException extends RequestException {

    private static final String DEFAULT_MESSAGE = "An exception has occurred during extraction of data from raw client request.";

    public RequestExtractionException(Throwable cause, RoutingContext routingContext) {
        super(DEFAULT_MESSAGE, cause, routingContext);
    }
}
