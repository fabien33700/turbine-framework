package io.turbine.core.errors.handling;

import io.reactivex.functions.Function;
import io.turbine.core.web.router.Response;

public interface ExceptionHandler extends Function<Throwable, Response> {

}
