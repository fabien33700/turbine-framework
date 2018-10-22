package io.turbine.core.model.web.handlers;

import io.reactivex.functions.Function;

public interface ResponsePrinter<Rp> extends Function<Rp, String> {
}
