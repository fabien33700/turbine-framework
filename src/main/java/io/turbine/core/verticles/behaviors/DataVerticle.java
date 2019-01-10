package io.turbine.core.verticles.behaviors;

import io.reactivex.Single;
import io.turbine.core.jdbc.queries.QueryBuilder;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLOptions;
import io.vertx.reactivex.ext.sql.SQLConnection;

/**
 * Defines the behavioral specification of a verticle which provides
 * JDBC asynchronous connection and querying capabilities.
 * Jdbc relies on the Vert.x JDBC async client to operate querying operation
 * in a non-blocking way, and especially on the RX flavor one, in order to allow
 * developper to handle SQL results with Reactive streams and operators.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public interface DataVerticle {

    /**
     * Provides the JDBC configuration JsonObject, which contains
     * required configuration such as JDBC Uri and credentials.
     * This object is used by the async client. See its documentation
     * to known with which key match right parameters
     * @return A JsonObject that wraps the DBMS connection configuration
     */
    JsonObject jdbcConfiguration();

    /**
     * Returns a QueryBuilderImpl which makes query building and
     * result transforming easier
     * @return A QueryBuilderImpl instance
     */
    QueryBuilder query();

    /**
     * Defines the SQL Options to set to each connection.
     * @see SQLOptions
     * @return A SQLOptions instance
     */
    SQLOptions sqlOptions();

    /**
     * Create a single source object, that will emit a SQLConnection
     * instance when the client has correctly initialized it.
     * @return A Single source that emits a SQLConnection instance.
     */
    Single<SQLConnection> connect();
}
