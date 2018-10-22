package io.turbine.core.model.web.handlers;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.turbine.core.model.web.router.Response;
import io.vertx.reactivex.ext.web.RoutingContext;

public interface RxRequestHandler<Rp> extends Function<RoutingContext, Single<Response<Rp>>> {
}
