package io.turbine.core.web.handlers;

import static io.turbine.core.web.handlers.ResponseAdapter.*;

public enum ResponseTypeEnum {

    JSON(jsonAdapter()),
    TEXT(plainTextAdapter()),
    XML(xmlAdapter());

    private final ResponseAdapter responseAdapter;


    ResponseTypeEnum(ResponseAdapter responseAdapter) {
        this.responseAdapter = responseAdapter;
    }

    public ResponseAdapter responseAdapter() {
        return responseAdapter;
    }
}
