package io.turbine.core.jdbc;

import io.reactivex.functions.Function;
import io.vertx.core.json.JsonObject;

/**
 * Defines a function that transform a individual result row from a query execution
 * to the desired R-typed result.
 *
 * @param <R> The type of the result extracted from the JsonObject row
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface ResultTransformer<R> extends Function<JsonObject, R> {}
