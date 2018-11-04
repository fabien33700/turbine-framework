package io.turbine.core.web.handlers;

import io.reactivex.functions.Consumer;
import io.vertx.reactivex.core.http.HttpServerResponse;

import static io.turbine.core.web.HttpConstants.ContentTypes.APPLICATION_JSON;
import static io.turbine.core.web.HttpConstants.ContentTypes.TEXT_PLAIN;
import static io.turbine.core.web.HttpConstants.ResponseHeaders.CONTENT_TYPE;

public interface ResponseAdapter extends Consumer<HttpServerResponse> {

    static ResponseAdapter create(String contentType) {
        return resp -> resp.putHeader(CONTENT_TYPE, contentType);
    }

    static ResponseAdapter create(String contentType, String encoding) {
        return resp -> resp.putHeader(CONTENT_TYPE, contentType + ";charset=" + encoding);
    }

    static ResponseAdapter jsonAdapter() {
        return create(APPLICATION_JSON, "utf-8");
    }

    static ResponseAdapter plainTextAdapter() {
        return create(TEXT_PLAIN, "utf-8");
    }
}
