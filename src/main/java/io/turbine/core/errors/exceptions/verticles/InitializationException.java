package io.turbine.core.errors.exceptions.verticles;

import io.turbine.core.verticles.BaseVerticle;

import static java.lang.String.format;

public final class InitializationException extends RuntimeException {

    private static final String GENERIC_MESSAGE = "An exception has occured that prevents the verticle %s from initialize itself.";

    public InitializationException(Throwable cause, BaseVerticle verticle) {
        super(format(GENERIC_MESSAGE, verticle.getClass().getName()), cause);
    }
}
