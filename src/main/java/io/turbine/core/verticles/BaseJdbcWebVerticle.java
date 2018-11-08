package io.turbine.core.verticles;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.turbine.core.jdbc.QueryBuilder;
import io.turbine.core.verticles.behaviors.JdbcVerticle;
import io.turbine.core.verticles.lifecycle.InitializationChain;
import io.vertx.ext.sql.SQLOptions;
import io.vertx.reactivex.ext.jdbc.JDBCClient;
import io.vertx.reactivex.ext.sql.SQLConnection;

import static io.vertx.reactivex.ext.jdbc.JDBCClient.createShared;

public abstract class BaseJdbcWebVerticle extends BaseWebVerticle implements JdbcVerticle {

    private JDBCClient jdbc;
    private final QueryBuilder queryBuilder = new QueryBuilder();

    @Override
    public final QueryBuilder query() {
        return queryBuilder;
    }

    @Override
    protected InitializationChain initialize() {
        return super.initialize().append(new Completable() {
             @Override
             protected void subscribeActual(CompletableObserver s) {
                 try {
                     BaseJdbcWebVerticle.this.jdbc = createShared(vertx, jdbcConfiguration());
                     connect()
                         .doOnSuccess(connection -> {
                             connection.close();
                             logger.info("Asynchronous JDBC client ready, connection to the DBMS tested OK");
                             s.onComplete();
                         })
                         .subscribe();
                 } catch (Throwable t) {
                     s.onError(t);
                 }
             }
         });
    }

    @Override
    public final Single<SQLConnection> connect() {
        return jdbc.rxGetConnection()
                .map(c -> c.setOptions(sqlOptions()));
    }

    @Override
    public SQLOptions sqlOptions() {
        return new SQLOptions().setAutoGeneratedKeys(true);
    }
}
