package io.turbine.core.jdbc.queries;

import io.reactivex.functions.Function;
import io.turbine.core.jdbc.queries.executions.QueryExecution;
import io.turbine.core.jdbc.results.ManyResults;
import io.turbine.core.jdbc.results.SingleResult;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;

import static io.turbine.core.json.JsonFormat.createJsonArray;

/**
 * An utility class that allows developer to updateWithParams asynchronously JDBC queries.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public final class QueryBuilderImpl implements QueryBuilder {

    @Override
    public final QueryExecution.Results single(final String sql, final Object... params) {
        return queryWithParams(sql, createJsonArray(params), SingleResult::new);
    }

    @Override
    public final QueryExecution.Results select(final String sql, final Object... params) {
        return queryWithParams(sql, createJsonArray(params), ManyResults::new);
    }

    @Override
    public final QueryExecution.Update update(final String sql, final Object... params) {
        return updateWithParams(sql, createJsonArray(params));
    }

    private QueryExecution.Results queryWithParams(final String sql, final JsonArray params,
                                                   Function<ResultSet, io.turbine.core.jdbc.results.Results> resultsFactory)
    {
        return connection -> connection
                .rxQueryWithParams(sql, params)
                .map(resultsFactory)
                .doFinally(connection::close);
    }

    private QueryExecution.Update updateWithParams(final String sql, JsonArray params) {
        return connection -> connection
                .rxUpdateWithParams(sql, params)
                .map(UpdateResult::getUpdated);
    }
}
