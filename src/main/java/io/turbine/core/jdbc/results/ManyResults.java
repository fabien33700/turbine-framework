package io.turbine.core.jdbc.results;

import io.turbine.core.errors.exceptions.jdbc.JdbcException;
import io.turbine.core.errors.exceptions.jdbc.NotSingleResultException;
import io.turbine.core.errors.exceptions.jdbc.TransformationException;
import io.turbine.core.jdbc.transformers.ResultTransformer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static io.turbine.core.json.JsonFormat.jsonArrayCollector;

/**
 * A Results implementation for gathering many results.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public class ManyResults implements Results {

    protected final List<JsonObject> rows;

    public ManyResults(ResultSet rs) {
        rows = new LinkedList<>(rs.getRows());
    }

    @Override
    public List<JsonObject> toList() {
        return Collections.unmodifiableList(rows);
    }

    @Override
    public <R> List<R> toTypedList(ResultTransformer<R> transformer) throws TransformationException {
        List<R> resultingList = new ArrayList<>();
        try {
            for (JsonObject object : this.rows) {
                resultingList.add(transformer.apply(object));
            }
        } catch (Exception ex) {
            throw new TransformationException(ex);
        }
        return Collections.unmodifiableList(resultingList);
    }

    @Override
    public JsonArray toArray() {
        return rows.stream().collect(jsonArrayCollector());
    }


    /*
     * ManyResults is not supposed to have only one single result,
     * it will throw appropriate exception if getSingle() or getTypedSingle() are called.
     */
    @Override
    public JsonObject getSingle() throws JdbcException {
        throw new NotSingleResultException();
    }

    @Override
    public <R> R getTypedSingle(ResultTransformer<R> transformer) throws JdbcException {
        throw new NotSingleResultException();
    }

    @Override
    public final int count() {
        return rows.size();
    }

    @Override
    public final boolean isSingle() {
        return count() <= 1;
    }

    @Override
    public final boolean isEmpty() {
        return rows.isEmpty();
    }
}
