package io.turbine.core.jdbc.results;

import io.turbine.core.errors.exceptions.jdbc.JdbcException;
import io.turbine.core.errors.exceptions.jdbc.NoResultException;
import io.turbine.core.errors.exceptions.jdbc.TransformationException;
import io.turbine.core.jdbc.transformers.ResultTransformer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

public class SingleResult extends ManyResults {
    SingleResult(ResultSet rs) {
        super(rs);
    }

    @Override
    public JsonObject getSingle() throws JdbcException {
        if (rows.isEmpty())
            throw new NoResultException();

        return rows.iterator().next();
    }

    @Override
    public <R> R getTypedSingle(ResultTransformer<R> transformer) throws JdbcException {
        try {
            return transformer.apply(getSingle());
        } catch (JdbcException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new TransformationException(ex);
        }
    }
}
