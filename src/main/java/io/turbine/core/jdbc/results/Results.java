package io.turbine.core.jdbc.results;

import io.turbine.core.errors.exceptions.jdbc.JdbcException;
import io.turbine.core.errors.exceptions.jdbc.TransformationException;
import io.turbine.core.jdbc.transformers.ResultTransformer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.List;

public interface Results {
    List<JsonObject> toList();

    <R> List<R> toTypedList(ResultTransformer<R> transformer) throws TransformationException;

    JsonArray toArray();

    JsonObject getSingle() throws JdbcException;

    <R> R getTypedSingle(ResultTransformer<R> transformer) throws JdbcException;

    int count();

    boolean isSingle();

    boolean isEmpty();

    static Results from(ResultSet rs) {
        int size = rs.getRows().size();
        return (size <= 1) ? new SingleResult(rs) : new ManyResults(rs);
    }
}
