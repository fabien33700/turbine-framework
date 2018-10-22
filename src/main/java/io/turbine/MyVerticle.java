package io.turbine;

import io.turbine.core.configuration.Configuration;
import io.turbine.core.json.JsonSource;
import io.turbine.core.model.web.router.Response;
import io.turbine.core.verticles.BaseJdbcWebVerticle;
import io.vertx.core.json.JsonObject;

import static io.reactivex.BackpressureStrategy.DROP;
import static io.reactivex.Single.just;
import static io.turbine.core.model.web.router.Response.ok;
import static java.time.Instant.now;

@Configuration
public class MyVerticle extends BaseJdbcWebVerticle {

    @Override
    public void start() throws Exception {

        router()
                .get("/now")
                .toFlowable(DROP)
                .doOnNext(jsonResponse(
                        rc -> ok(JsonSource.from(now()))
                )).subscribe();


        router()
                .get("/users")
                .toFlowable(DROP)
                .doOnNext(rxJsonResponse(
                        rc -> connect()
                            .flatMap(query().asJson("SELECT * FROM users WHERE 0"))
                            .map(Response::ok)
                            .onErrorResumeNext(this::handleException)
                )).subscribe();

        listen().subscribe(logServerStarted());
    }

    @Override
    public JsonObject jdbcConfiguration() {
        return readConfig("db");
    }
}
