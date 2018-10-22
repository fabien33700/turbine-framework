package io.turbine.core.jdbc;

import io.turbine.core.errors.exceptions.jdbc.NoResultException;
import io.turbine.core.json.JsonSource;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collector;

import static io.turbine.core.utils.functional.FnUtils.safelyApply;
import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.toList;

public final class QueryBuilder {

    /*private <I, R> QueryExecutor<I>
    query(String sql, ResultTransformer<R> transformer, Collector<R, ?, I> collector) {
        return connection -> connection
                .rxQuery(sql)
                .map(rs -> rs.getRows().stream()
                        .flatMap(safelyApply(transformer))
                        .collect(collector))
                .doFinally(connection::close);
    }*/

    public final QueryExecutor<JsonSource> singleAsJson(final String sql) {
        return connection -> connection
                .rxQuery(sql)
                .map(rs -> rs.getRows().stream()
                        .findFirst()
                        .orElseThrow(NoResultException::new))
                .map(JsonSource::from)
                .doFinally(connection::close);
    }

    public final QueryExecutor<JsonSource> asJson(final String sql) {
        final Collector<JsonObject, ?, JsonArray> jsonArrayCollector = of(
                JsonArray::new, JsonArray::add, JsonArray::addAll);

        return connection -> connection
                    .rxQuery(sql)
                    .map(rs -> rs.getRows().stream()
                            .collect(jsonArrayCollector))
                    .map(JsonSource::from)
                .doFinally(connection::close);
    }

    /*public final QueryExecutor<Object> asJson(String sql) {
        return connection -> connection
                .rxQuery(sql)
                .map(rs -> (Object) rs.getRows())
                .doFinally(connection::close);
    }

    public final <R> QueryExecutor<List<R>> asListOf(String sql, ResultTransformer<R> transformer) {
        return query(sql, transformer, toList());
    }

    public <R> QueryExecutor<R> single(String sql, ResultTransformer<R> transformer) {
        return connection -> connection
                .rxQuery(sql)
                .map(rs -> rs.getRows().stream()
                        .flatMap(safelyApply(transformer))
                        .findFirst()
                        .orElseThrow(() -> new Exception("Pas de résultats")))
                .doFinally(connection::close);
    }

    public QueryExecutor<Object> single(String sql) {
        return connection -> connection
                .rxQuery(sql)
                .map(rs -> rs.getRows().stream()
                        .map(o -> (Object) o)
                        .findFirst()
                        .orElseThrow(() -> new Exception("Pas de résultats")))
                .doFinally(connection::close);
    }

    public final QueryExecutor<JsonArray> asJsonArray(String sql) {
        final Collector<JsonObject, ?, JsonArray> jsonArrayCollector = of(
                JsonArray::new, JsonArray::add, JsonArray::addAll);

        return query(sql, o -> o, jsonArrayCollector);
    }

    public final QueryExecutor<List<JsonObject>> asJsonObjects(String sql) {
        return asListOf(sql, o -> o);
    }
*/


}
