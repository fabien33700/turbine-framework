package io.turbine.core.web.handlers;

import io.reactivex.functions.Function;
import io.turbine.core.web.router.Response;
import io.vertx.reactivex.ext.web.RoutingContext;

public interface RequestHandler<Rp> extends Function<RoutingContext, Response<Rp>> {
}
