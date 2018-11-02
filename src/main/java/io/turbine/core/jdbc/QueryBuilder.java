package io.turbine.core.jdbc;

import io.turbine.core.errors.exceptions.jdbc.NoResultException;
import io.turbine.core.jdbc.transformers.ModelTransformer;
import io.turbine.core.json.JsonSerializable;
import io.turbine.core.json.JsonSource;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collector;

import static java.util.stream.Collector.of;
import static java.util.stream.Collectors.toList;

public final class QueryBuilder {

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
        return connection -> connection
                    .rxQuery(sql)
                    .map(rs -> rs.getRows().stream()
                            .collect(JsonSource.JSON_ARRAY_COLLECTOR))
                    .map(JsonSource::from)
                .doFinally(connection::close);
    }
}
