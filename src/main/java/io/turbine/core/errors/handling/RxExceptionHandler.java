package io.turbine.core.errors.handling;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.turbine.core.model.web.router.Response;

public interface RxExceptionHandler<Rp> extends Function<Throwable, Single<Response<Rp>>> {
}
