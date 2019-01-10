package io.turbine.core.jdbc.queries.executions;

import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.vertx.reactivex.ext.sql.SQLConnection;

@FunctionalInterface
public interface QueryExecution<R> extends Function<SQLConnection, SingleSource<R>> {

    /**
     * Defines a function that supplies a SingleSource of a Results instance from a SQLConnection.
     * This is actually used to operate a flatMap() on a Single<SQLConnection> (usually
     * provided by DataVerticle::connect) to get a Single<Results>.
     *
     * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
     */
    @FunctionalInterface
    interface Results extends QueryExecution<io.turbine.core.jdbc.results.Results> {}

    /**
     * Defines a function that supplies a SingleSource of the number of updated rows
     * after the execution of an update from a SQLConnection.
     * This is actually used to operate a flatMap() on a Single<SQLConnection> (usually
     * provided by DataVerticle::connect) to get a Single<Integer>.
     *
     * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
     */
    @FunctionalInterface
    interface Update extends QueryExecution<Integer> {}
}
