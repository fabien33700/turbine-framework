package io.turbine.core.jdbc;

import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.turbine.core.jdbc.results.Results;
import io.vertx.reactivex.ext.sql.SQLConnection;

/**
 * Defines a function that supplies a SingleSource of a Results instance from a SQLConnection.
 * This is actually use to operate a flatMap() on a Single<SQLConnection> (usually
 * provided by JdbcVerticle::connect) to get a Single<Results>.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface QueryExecution extends Function<SQLConnection, SingleSource<Results>> {}
