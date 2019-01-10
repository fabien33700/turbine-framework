package io.turbine.core.jdbc.queries;

import io.turbine.core.jdbc.queries.executions.QueryExecution;

public interface QueryBuilder {

    /**
     * Execute a SQL prepared statement and wraps the resulting data
     * as a SingleResult.
     * @param sql The SQL of the query
     * @param params  The parameters to fill the statement
     * @see QueryExecution.Results
     * @return The QueryExecution instance
     */
    QueryExecution.Results single(String sql, Object... params);

    /**
     * Execute a SQL prepared statement and wraps the resulting data
     * as a ManyResults instance.
     * @param sql The SQL of the query
     * @param params  The parameters to fill the statement
     * @see QueryExecution.Results
     * @return The QueryExecution instance
     */
    QueryExecution.Results select(String sql, Object... params);

    /**
     * Execute a SQL prepared statement of an update statement (UPDATE / INSERT / DELETE)
     * and return the number of updated/affected rows.
     * @param sql The SQL of the query
     * @param params  The parameters to fill the statement
     * @see QueryExecution.Update
     * @return The Update instance
     */
    QueryExecution.Update update(String sql, Object... params);
}
