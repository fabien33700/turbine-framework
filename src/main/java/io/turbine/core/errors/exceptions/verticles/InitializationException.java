package io.turbine.core.errors.exceptions.verticles;

import io.turbine.core.verticles.BaseVerticle;

import static java.lang.String.format;

/**
 * This exception can be thrown by a Verticle when it failed to initialize itself.
 * Initialization method is asynchronous, and realized through a CompletableChain.
 *
 * When an exception is caught during the execution of this chain, it is wrapped into
 * an exception like this, which is thrown in turn.
 *
 * @see io.turbine.core.verticles.BaseVerticle.CompletableChain
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public final class InitializationException extends RuntimeException {
    /**
     * The constant holding the exception defaut message
     */
    private static final String GENERIC_MESSAGE = "An exception has occured that prevents the verticle %s from initialize itself.";

    public InitializationException(Throwable cause, BaseVerticle verticle) {
        super(format(GENERIC_MESSAGE, verticle.getClass().getName()), cause);
    }
}
