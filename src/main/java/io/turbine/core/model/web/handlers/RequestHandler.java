package io.turbine.core.model.web.handlers;

import io.reactivex.functions.Function;
import io.turbine.core.model.web.router.Response;
import io.vertx.reactivex.ext.web.RoutingContext;

public interface RequestHandler<Rp> extends Function<RoutingContext, Response<Rp>> {
}
