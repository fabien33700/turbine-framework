package io.turbine;

import io.reactivex.BackpressureStrategy;
import io.turbine.core.configuration.Configuration;
import io.turbine.core.verticles.BaseWebVerticle;

@Configuration
public class MyVerticle extends BaseWebVerticle {

    @Override
    public void start() throws Exception {
        router()
                .get("/")
                .toFlowable(BackpressureStrategy.DROP)
                .doOnNext(rc -> rc.response().end("Hello world"))
                .subscribe();

        listen().subscribe(logServerStarted());
    }


}
