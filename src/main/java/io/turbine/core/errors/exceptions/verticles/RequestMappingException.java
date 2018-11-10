package io.turbine.core.errors.exceptions.verticles;

import io.turbine.core.web.mapping.RequestHandling;

import java.lang.reflect.Method;

import static java.lang.String.format;

/**
 * This exception can be thrown by a Web verticle during TODO
 *
 * When an exception is caught during the execution of this chain, it is wrapped into
 * an exception like this, which is thrown in turn.
 *
 * @author Fabien <fabien DOT lehouedec AT gmail DOT com>
 */
public final class RequestMappingException extends RuntimeException {
    private static final String GENERIC_MESSAGE = "Bad request mapping definition of the method %s() to the route %s %s in verticle %s : ";

    public RequestMappingException(Class<?> clazz, Method method, RequestHandling requestHandling, Throwable cause)  {
        super(format(GENERIC_MESSAGE,
                method.getName(), requestHandling.method(), requestHandling.path(),
                clazz.getSimpleName()), cause);
    }
}
