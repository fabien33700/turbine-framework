package io.turbine.core.errors.handling;

import io.reactivex.functions.Function;
import io.turbine.core.web.router.Response;

public interface ExceptionHandler<Rp> extends Function<Throwable, Response<Rp>> {

}
