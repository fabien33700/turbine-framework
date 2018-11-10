package io.turbine.core.web.handlers;

import io.reactivex.functions.Consumer;
import io.vertx.reactivex.core.http.HttpServerResponse;

import static io.turbine.core.web.HttpConstants.ContentTypes.*;
import static io.turbine.core.web.HttpConstants.ResponseHeaders.CONTENT_TYPE;

public interface ResponseAdapter extends Consumer<HttpServerResponse> {

    String DEFAULT_ENCODING = "utf-8";

    static ResponseAdapter create(String contentType) {
        return resp -> resp.putHeader(CONTENT_TYPE, contentType);
    }

    static ResponseAdapter create(String contentType, String encoding) {
        return resp -> resp.putHeader(CONTENT_TYPE, contentType + ";charset=" + encoding);
    }

    static ResponseAdapter jsonAdapter() {
        return jsonAdapter(DEFAULT_ENCODING);
    }


    static ResponseAdapter jsonAdapter(String charset) {
        return create(APPLICATION_JSON, charset);
    }

    static ResponseAdapter plainTextAdapter() {
        return plainTextAdapter(DEFAULT_ENCODING);
    }

    static ResponseAdapter plainTextAdapter(String charset) {
        return create(TEXT_PLAIN, charset);
    }

    static ResponseAdapter xmlAdapter() {
        return xmlAdapter(DEFAULT_ENCODING);
    }

    static ResponseAdapter xmlAdapter(String charset) {
        return create(APPLICATION_XML, charset);
    }
}
