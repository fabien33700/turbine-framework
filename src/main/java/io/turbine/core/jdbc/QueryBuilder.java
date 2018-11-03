package io.turbine.core.jdbc;

import io.turbine.core.errors.exceptions.jdbc.NoResultException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.List;

public final class QueryBuilder {

    public final QueryExecutor<JsonObject> single(final String sql) {
        return connection -> connection
                .rxQuery(sql)
                .map(rs -> rs.getRows().stream()
                        .findFirst()
                        .orElseThrow(NoResultException::new))
                .doFinally(connection::close);
    }

    public final QueryExecutor<List<JsonObject>> list(final String sql) {
        return connection -> connection
                    .rxQuery(sql)
                    .map(ResultSet::getRows)
                .doFinally(connection::close);
    }
}
