package io.turbine.core.web.cors;

import io.vertx.reactivex.ext.web.handler.CorsHandler;

import static io.vertx.core.http.HttpMethod.*;

public class CorsHandlers {

    // TODO: Find better name and other common Cors configuration.
    public static CorsHandler allowAllCors() {
        return CorsHandler.create("*")
                .allowedMethod(GET)
                .allowedMethod(POST)
                .allowedMethod(OPTIONS)
                .allowedHeader("Authorization")
                .allowedHeader("Access-Control-Allow-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Content-Type");
    }
}