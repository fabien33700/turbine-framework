package io.turbine.core.jdbc;

import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.turbine.core.jdbc.results.Results;
import io.vertx.reactivex.ext.sql.SQLConnection;

/**
 * Defines a function that supplies a SingleSource of a R-typed result from a SQLConnection.
 * This is actually use to operate a flatMap() on a Single<SQLConnection> (usually
 * provided by JdbcVerticle::connect) to obtain a Single<R> where R represents the type
 * of the result returned by a query execution.
 *
 * QueryBuilder utility class provides several methods that implements this function with different
 * result types, such as JsonArray, List<JsonObject>, ...
 *
 * Given that query execution with Vertx RX rxQuery() method returns a Vert.x ResultSet instance,
 * which itself contains a List<JsonObject> supplied by the getRows() method (JsonObject represent
 * one ResultSet row), QueryBuilder's methods will use ResultTransformer implementations to
 * transform the ResultSet to the desired result type.
 *
 * @param <R> The type of the query result
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface QueryExecutor<R> extends Function<SQLConnection, SingleSource<Results>> {}
