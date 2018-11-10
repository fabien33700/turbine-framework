package io.turbine.core.jdbc;

import io.reactivex.functions.Function;
import io.turbine.core.jdbc.results.ManyResults;
import io.turbine.core.jdbc.results.Results;
import io.turbine.core.jdbc.results.SingleResult;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

import static io.turbine.core.json.JsonFormat.createJsonArray;

/**
 * An utility class that allows developer to execute asynchronously JDBC queries.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public final class QueryBuilder {

    /**
     * Execute a SQL prepared statement and wraps the resulting data
     * as a SingleResult.
     * @param sql The SQL of the query
     * @param params  The parameters to fill the statement
     * @see QueryExecution
     * @return The QueryExecution instance
     */
    public final QueryExecution single(final String sql, final Object... params) {
        return execute0(sql, createJsonArray(params), SingleResult::new);
    }

    /**
     * Execute a SQL prepared statement and wraps the resulting data
     * as a ManyResults instance.
     * @param sql The SQL of the query
     * @param params  The parameters to fill the statement
     * @see QueryExecution
     * @return The QueryExecution instance
     */
    public final QueryExecution many(final String sql, final Object... params) {
        return execute0(sql, createJsonArray(params), ManyResults::new);
    }

    private QueryExecution execute0(final String sql, final JsonArray params,
                                    Function<ResultSet, Results> resultsFactory)
    {
        return connection -> connection
                .rxQueryWithParams(sql, params)
                .map(resultsFactory)
                .doFinally(connection::close);
    }
}
