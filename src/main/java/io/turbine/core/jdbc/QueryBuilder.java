package io.turbine.core.jdbc;

import io.turbine.core.jdbc.results.Results;
import io.vertx.core.json.JsonArray;

import java.util.stream.Stream;

import static io.turbine.core.json.JsonFormat.jsonArrayCollector;

public final class QueryBuilder {

    public final QueryExecutor<Results> single(final String sql, final Object... params) {
        JsonArray array = Stream.of(params).collect(jsonArrayCollector());

        return connection -> connection
                .rxQueryWithParams(sql, array)
                .map(Results::from)
                .doFinally(connection::close);
    }

    public final QueryExecutor<Results> many(final String sql, final Object... params) {
        JsonArray array = Stream.of(params).collect(jsonArrayCollector());

        return connection -> connection
                .rxQueryWithParams(sql, array)
                .map(Results::manyFrom)
                .doFinally(connection::close);
    }
}
