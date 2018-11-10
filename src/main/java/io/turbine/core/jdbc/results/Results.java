package io.turbine.core.jdbc.results;

import io.turbine.core.errors.exceptions.jdbc.JdbcException;
import io.turbine.core.errors.exceptions.jdbc.TransformationException;
import io.turbine.core.jdbc.transformers.ResultTransformer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * An object that contains Jdbc Query execution results, build from
 * a Vert.x SQL ResultSet instance.
 * Results have also to manage result transformation from generic container type
 * JsonObject (similar to a Map with String typed key) to user-defined model instance.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface Results {
    /**
     * Get the query results as raw list of JsonObject.
     * @return A list of JsonObject representing each row
     */
    List<JsonObject> toList();

    /**
     * Get the query results after transforming each of them
     * with the given ResultTransformer.
     * @param transformer The result transformer
     * @param <R> The type of transformed model instances
     * @return A list of R-typed instance
     * @throws TransformationException An error has occurred while applying the transformer
     */
    <R> List<R> toTypedList(ResultTransformer<R> transformer) throws TransformationException;

    /**
     * Get the query results as a raw JsonArray.
     * @return A JsonArray instance representing all rows
     */
    JsonArray toArray();

    /**
     * Get the single and only result from the query execution.
     * @return A list of R-typed instance
     * @throws JdbcException An error has occurred during the query execution
     */
    JsonObject getSingle() throws JdbcException;

    /**
     * Get the single and only result from the query execution, after
     * transforming it to a model instance.
     * @param transformer The result transformer
     * @param <R> The type of transformed model instance
     * @return A R-typed instance
     * @throws JdbcException An error has occurred during the query execution
     */
    <R> R getTypedSingle(ResultTransformer<R> transformer) throws JdbcException;

    /**
     * Gets the count of result.
     * @return The number of rows
     */
    int count();

    /**
     * Indicates whether if this is a single result.
     * @return true if the result is single, false otherwise
     */
    boolean isSingle();

    /**
     * Indicates whether if the result container is empty.
     * @return true if there is no result, false otherwise
     */
    boolean isEmpty();
}
